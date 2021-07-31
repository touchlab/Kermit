//
// Created by Jamie Lynch on 30/11/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import "BugsnagSessionTrackingApiClient.h"

#import "BugsnagConfiguration+Private.h"
#import "BugsnagSessionTrackingPayload.h"
#import "BugsnagSessionFileStore.h"
#import "BugsnagLogger.h"
#import "BugsnagSession.h"
#import "BugsnagSession+Private.h"
#import "BSG_RFC3339DateTool.h"

@interface BugsnagSessionTrackingApiClient ()
@property (nonatomic) NSMutableSet *activeIds;
@property(nonatomic) BugsnagConfiguration *config;
@end


@implementation BugsnagSessionTrackingApiClient

- (instancetype)initWithConfig:(BugsnagConfiguration *)configuration queueName:(NSString *)queueName notifier:(BugsnagNotifier *)notifier {
    if ((self = [super initWithSession:configuration.session queueName:queueName])) {
        _activeIds = [NSMutableSet new];
        _config = configuration;
        _notifier = notifier;
    }
    return self;
}

- (NSOperation *)deliveryOperation {
    return [NSOperation new];
}

- (void)deliverSessionsInStore:(BugsnagSessionFileStore *)store {
    NSString *apiKey = [self.config.apiKey copy];
    NSURL *sessionURL = [self.config.sessionURL copy];

    if (!apiKey) {
        bsg_log_err(@"No API key set. Refusing to send sessions.");
        return;
    }

    [[store allFilesByName] enumerateKeysAndObjectsUsingBlock:^(NSString *fileId, NSDictionary *fileContents, __attribute__((unused)) BOOL *stop) {
        // De-duplicate files as deletion of the file is asynchronous and so multiple calls
        // to this method will result in multiple send requests
        @synchronized (self.activeIds) {
            if ([self.activeIds containsObject:fileId]) {
                return;
            }
            [self.activeIds addObject:fileId];
        }

        BugsnagSession *session = [[BugsnagSession alloc] initWithDictionary:fileContents];

        [self.sendQueue addOperationWithBlock:^{
            BugsnagSessionTrackingPayload *payload = [[BugsnagSessionTrackingPayload alloc]
                initWithSessions:@[session]
                          config:self.config
                    codeBundleId:self.codeBundleId
                        notifier:self.notifier];
            NSMutableDictionary *data = [payload toJson];
            NSDictionary *HTTPHeaders = @{
                BugsnagHTTPHeaderNameApiKey: apiKey ?: @"",
                BugsnagHTTPHeaderNamePayloadVersion: @"1.0",
                BugsnagHTTPHeaderNameSentAt: [BSG_RFC3339DateTool stringFromDate:[NSDate date]]
            };
            [self sendJSONPayload:data headers:HTTPHeaders toURL:sessionURL
                completionHandler:^(BugsnagApiClientDeliveryStatus status, NSError *error) {
                switch (status) {
                    case BugsnagApiClientDeliveryStatusDelivered:
                        bsg_log_info(@"Sent session %@", session.id);
                        [store deleteFileWithId:fileId];
                        break;
                    case BugsnagApiClientDeliveryStatusFailed:
                        bsg_log_warn(@"Failed to send sessions: %@", error);
                        break;
                    case BugsnagApiClientDeliveryStatusUndeliverable:
                        bsg_log_warn(@"Failed to send sessions: %@", error);
                        [store deleteFileWithId:fileId];
                        break;
                }
                @synchronized (self.activeIds) {
                    [self.activeIds removeObject:fileId];
                }
            }];
        }];
    }];
}

@end
