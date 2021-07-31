//
//  BSGEventUploadObjectOperation.m
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadObjectOperation.h"

#import "BugsnagEvent+Private.h"
#import "BugsnagLogger.h"

@implementation BSGEventUploadObjectOperation

- (instancetype)initWithEvent:(BugsnagEvent *)event delegate:(id<BSGEventUploadOperationDelegate>)delegate {
    if ((self = [super initWithDelegate:delegate])) {
        _event = event;
    }
    return self;
}

- (BugsnagEvent *)loadEventAndReturnError:(__attribute__((unused)) NSError * __autoreleasing *)errorPtr {
    [self.event symbolicateIfNeeded];
    return self.event;
}

- (BOOL)shouldStoreEventPayloadForRetry {
    return YES;
}

- (NSString *)name {
    return self.event.description;
}

@end
