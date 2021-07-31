//
//  BSGEventUploadOperation.h
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BugsnagApiClient.h"

@class BugsnagConfiguration;
@class BugsnagEvent;
@class BugsnagNotifier;

NS_ASSUME_NONNULL_BEGIN

@protocol BSGEventUploadOperationDelegate;

/**
 * The abstract base class for all event upload operations.
 *
 * Implements an asynchronous NSOperation and the core logic for checking whether an event should be sent, and uploading it.
 */
@interface BSGEventUploadOperation : NSOperation

- (instancetype)init NS_UNAVAILABLE;

- (instancetype)initWithDelegate:(id<BSGEventUploadOperationDelegate>)delegate;

@property (readonly, weak, nonatomic) id<BSGEventUploadOperationDelegate> delegate;

// MARK: Subclassing

/// Must be implemented by all subclasses.
- (nullable BugsnagEvent *)loadEventAndReturnError:(NSError **)errorPtr;

/// Called if the event should not be sent or failed to upload in a non-retrable way.
///
/// To be implemented by subclasses that load their data from a file.
- (void)deleteEvent;

/// Whether the payload should be stored so that it can be retried later.
@property (readonly, nonatomic) BOOL shouldStoreEventPayloadForRetry;

@end

// MARK: -

@protocol BSGEventUploadOperationDelegate <NSObject>

@property (readonly, nonatomic) BugsnagApiClient *apiClient;

@property (readonly, nonatomic) BugsnagConfiguration *configuration;

@property (readonly, nonatomic) BugsnagNotifier *notifier;

- (void)storeEventPayload:(NSDictionary *)eventPayload;

@end

NS_ASSUME_NONNULL_END
