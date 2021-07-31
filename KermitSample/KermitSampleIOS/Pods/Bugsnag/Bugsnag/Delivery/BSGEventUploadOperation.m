//
//  BSGEventUploadOperation.m
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadOperation.h"

#import "BSGFileLocations.h"
#import "BSGInternalErrorReporter.h"
#import "BSG_RFC3339DateTool.h"
#import "BugsnagAppWithState+Private.h"
#import "BugsnagConfiguration+Private.h"
#import "BugsnagError+Private.h"
#import "BugsnagEvent+Private.h"
#import "BugsnagKeys.h"
#import "BugsnagLogger.h"
#import "BugsnagNotifier.h"


static NSString * const EventPayloadVersion = @"4.0";

typedef NS_ENUM(NSUInteger, BSGEventUploadOperationState) {
    BSGEventUploadOperationStateReady,
    BSGEventUploadOperationStateExecuting,
    BSGEventUploadOperationStateFinished,
};

@interface BSGEventUploadOperation ()

@property (nonatomic) BSGEventUploadOperationState state;

@end

// MARK: -

@implementation BSGEventUploadOperation

- (instancetype)initWithDelegate:(id<BSGEventUploadOperationDelegate>)delegate {
    if ((self = [super init])) {
        _delegate = delegate;
    }
    return self;
}

- (void)runWithDelegate:(id<BSGEventUploadOperationDelegate>)delegate completionHandler:(nonnull void (^)(void))completionHandler {
    bsg_log_debug(@"Preparing event %@", self.name);
    
    NSError *error = nil;
    BugsnagEvent *event = [self loadEventAndReturnError:&error];
    if (!event) {
        bsg_log_err(@"Failed to load event %@ due to error %@", self.name, error);
        if (!(error.domain == NSCocoaErrorDomain && error.code == NSFileReadNoSuchFileError)) {
            [self deleteEvent];
        }
        completionHandler();
        return;
    }
    
    BugsnagConfiguration *configuration = delegate.configuration;
    
    if (!configuration.shouldSendReports || ![event shouldBeSent]) {
        bsg_log_info(@"Discarding event %@ because releaseStage not in enabledReleaseStages", self.name);
        [self deleteEvent];
        completionHandler();
        return;
    }
    
    NSString *errorClass = event.errors.firstObject.errorClass;
    if ([configuration shouldDiscardErrorClass:errorClass]) {
        bsg_log_info(@"Discarding event %@ because errorClass \"%@\" matches configuration.discardClasses", self.name, errorClass);
        [self deleteEvent];
        completionHandler();
        return;
    }
    
    for (BugsnagOnSendErrorBlock block in configuration.onSendBlocks) {
        @try {
            if (!block(event)) {
                [self deleteEvent];
                completionHandler();
                return;
            }
        } @catch (NSException *exception) {
            bsg_log_err(@"Ignoring exception thrown by onSend callback: %@", exception);
        }
    }
    
    NSDictionary *eventPayload;
    @try {
        eventPayload = [event toJsonWithRedactedKeys:configuration.redactedKeys];
    } @catch (NSException *exception) {
        bsg_log_err(@"Discarding event %@ because an exception was thrown by -toJsonWithRedactedKeys: %@", self.name, exception);
        [self deleteEvent];
        completionHandler();
        return;
    }
    
    NSString *apiKey = event.apiKey ?: configuration.apiKey;
    
    NSMutableDictionary *requestPayload = [NSMutableDictionary dictionary];
    requestPayload[BSGKeyApiKey] = apiKey;
    requestPayload[BSGKeyEvents] = @[eventPayload];
    requestPayload[BSGKeyNotifier] = [delegate.notifier toDict];
    requestPayload[BSGKeyPayloadVersion] = EventPayloadVersion;
    
    NSMutableDictionary *requestHeaders = [NSMutableDictionary dictionary];
    requestHeaders[BugsnagHTTPHeaderNameApiKey] = apiKey;
    requestHeaders[BugsnagHTTPHeaderNamePayloadVersion] = EventPayloadVersion;
    requestHeaders[BugsnagHTTPHeaderNameSentAt] = [BSG_RFC3339DateTool stringFromDate:[NSDate date]];
    requestHeaders[BugsnagHTTPHeaderNameStacktraceTypes] = [event.stacktraceTypes componentsJoinedByString:@","];
    
    NSURL *notifyURL = configuration.notifyURL;
    if (!notifyURL) {
        bsg_log_err(@"Could not upload event %@ because notifyURL was nil", self.name);
        completionHandler();
        return;
    }
    
    [delegate.apiClient sendJSONPayload:requestPayload headers:requestHeaders toURL:notifyURL
                      completionHandler:^(BugsnagApiClientDeliveryStatus status, __attribute__((unused)) NSError *deliveryError) {
        
        switch (status) {
            case BugsnagApiClientDeliveryStatusDelivered:
                bsg_log_debug(@"Uploaded event %@", self.name);
                [self deleteEvent];
                break;
                
            case BugsnagApiClientDeliveryStatusFailed:
                bsg_log_debug(@"Upload failed; will retry event %@", self.name);
                if (self.shouldStoreEventPayloadForRetry) {
                    [delegate storeEventPayload:eventPayload];
                }
                break;
                
            case BugsnagApiClientDeliveryStatusUndeliverable:
                bsg_log_debug(@"Upload failed; will discard event %@", self.name);
                [self deleteEvent];
                break;
        }
        
        completionHandler();
    }];
}

// MARK: Subclassing

- (BugsnagEvent *)loadEventAndReturnError:(__attribute__((unused)) NSError * __autoreleasing *)errorPtr {
    // Must be implemented by all subclasses
    [self doesNotRecognizeSelector:_cmd];
    return nil;
}

- (void)deleteEvent {
}

// MARK: Asynchronous NSOperation implementation

- (void)start {
    if ([self isCancelled]) {
        [self setFinished];
        return;
    }
    
    id delegate = self.delegate;
    if (!delegate) {
        bsg_log_err(@"Upload operation %@ has no delegate", self);
        [self setFinished];
        return;
    }
    
    [self willChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
    self.state = BSGEventUploadOperationStateExecuting;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
    
    @try {
        [self runWithDelegate:delegate completionHandler:^{
            [self setFinished];
        }];
    } @catch (NSException *exception) {
        [BSGInternalErrorReporter.sharedInstance reportException:exception diagnostics:nil groupingHash:
         [NSString stringWithFormat:@"BSGEventUploadOperation -[runWithDelegate:completionHandler:] %@ %@",
          exception.name, exception.reason]];
        [self setFinished];
    }
}

- (void)setFinished {
    if (self.state == BSGEventUploadOperationStateFinished) {
        return;
    }
    [self willChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
    [self willChangeValueForKey:NSStringFromSelector(@selector(isFinished))];
    self.state = BSGEventUploadOperationStateFinished;
    [self didChangeValueForKey:NSStringFromSelector(@selector(isExecuting))];
    [self didChangeValueForKey:NSStringFromSelector(@selector(isFinished))];
}

- (BOOL)isAsynchronous {
    return YES;
}

- (BOOL)isReady {
    return self.state == BSGEventUploadOperationStateReady;
}

- (BOOL)isExecuting {
    return self.state == BSGEventUploadOperationStateExecuting;
}

- (BOOL)isFinished {
    return self.state == BSGEventUploadOperationStateFinished;
}

@end
