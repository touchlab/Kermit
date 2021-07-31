//
// Created by Jamie Lynch on 04/12/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NSString * BugsnagHTTPHeaderName NS_TYPED_ENUM;

extern BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameApiKey;
extern BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameIntegrity;
extern BugsnagHTTPHeaderName const BugsnagHTTPHeaderNamePayloadVersion;
extern BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameSentAt;
extern BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameStacktraceTypes;

typedef NS_ENUM(NSInteger, BugsnagApiClientDeliveryStatus) {
    /// The payload was delivered successfully and can be deleted.
    BugsnagApiClientDeliveryStatusDelivered,
    /// The payload was not delivered but can be retried, e.g. when there was a loss of connectivity.
    BugsnagApiClientDeliveryStatusFailed,
    /// The payload cannot be delivered and should be deleted without attempting to retry.
    BugsnagApiClientDeliveryStatusUndeliverable,
};

@interface BugsnagApiClient : NSObject

- (instancetype)initWithSession:(nullable NSURLSession *)session queueName:(NSString *)queueName;

/**
 * Send outstanding reports
 */
- (void)flushPendingData;

- (NSOperation *)deliveryOperation;

- (void)sendJSONPayload:(NSDictionary *)payload
                headers:(NSDictionary<BugsnagHTTPHeaderName, NSString *> *)headers
                  toURL:(NSURL *)url
      completionHandler:(void (^)(BugsnagApiClientDeliveryStatus status, NSError * _Nullable error))completionHandler;

+ (NSString *)SHA1HashStringWithData:(NSData *)data;

@property (readonly, nonatomic) NSOperationQueue *sendQueue;

@end

NS_ASSUME_NONNULL_END
