//
// Created by Jamie Lynch on 04/12/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import "BugsnagApiClient.h"

#import "BugsnagConfiguration.h"
#import "Bugsnag.h"
#import "BugsnagKeys.h"
#import "BugsnagLogger.h"
#import "BSGJSONSerialization.h"

#import <CommonCrypto/CommonCrypto.h>

BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameApiKey             = @"Bugsnag-Api-Key";
BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameIntegrity          = @"Bugsnag-Integrity";
BugsnagHTTPHeaderName const BugsnagHTTPHeaderNamePayloadVersion     = @"Bugsnag-Payload-Version";
BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameSentAt             = @"Bugsnag-Sent-At";
BugsnagHTTPHeaderName const BugsnagHTTPHeaderNameStacktraceTypes    = @"Bugsnag-Stacktrace-Types";

typedef NS_ENUM(NSInteger, HTTPStatusCode) {
    /// 402 Payment Required: a nonstandard client error status response code that is reserved for future use.
    ///
    /// This status code is returned by ngrok when a tunnel has expired.
    HTTPStatusCodePaymentRequired = 402,
    
    /// 407 Proxy Authentication Required: the request has not been applied because it lacks valid authentication credentials
    /// for a proxy server that is between the browser and the server that can access the requested resource.
    HTTPStatusCodeProxyAuthenticationRequired = 407,
    
    /// 408 Request Timeout: the server would like to shut down this unused connection.
    HTTPStatusCodeClientTimeout = 408,
    
    /// 429 Too Many Requests: the user has sent too many requests in a given amount of time ("rate limiting").
    HTTPStatusCodeTooManyRequests = 429,
};

@interface BugsnagApiClient()
@property (nonatomic, strong) NSURLSession *session;
@end

@implementation BugsnagApiClient

- (instancetype)initWithSession:(nullable NSURLSession *)session queueName:(NSString *)queueName {
    if ((self = [super init])) {
        _sendQueue = [NSOperationQueue new];
        _sendQueue.maxConcurrentOperationCount = 1;
        if ([_sendQueue respondsToSelector:@selector(qualityOfService)]) {
            _sendQueue.qualityOfService = NSQualityOfServiceUtility;
        }
        _sendQueue.name = queueName;
        _session = session ?: [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration ephemeralSessionConfiguration]];
    }
    return self;
}

- (void)flushPendingData {
    [self.sendQueue cancelAllOperations];
    NSOperation *delay = [NSBlockOperation blockOperationWithBlock:^{ [NSThread sleepForTimeInterval:1]; }];
    NSOperation *deliver = [self deliveryOperation];
    [deliver addDependency:delay];
    [self.sendQueue addOperations:@[delay, deliver] waitUntilFinished:NO];
}

- (NSOperation *)deliveryOperation {
    bsg_log_err(@"Should override deliveryOperation in subclass");
    return [NSOperation new];
}

#pragma mark - Delivery

- (void)sendJSONPayload:(NSDictionary *)payload
                headers:(NSDictionary<BugsnagHTTPHeaderName, NSString *> *)headers
                  toURL:(NSURL *)url
      completionHandler:(void (^)(BugsnagApiClientDeliveryStatus status, NSError * _Nullable error))completionHandler {
    
    if (![BSGJSONSerialization isValidJSONObject:payload]) {
        bsg_log_err(@"Error: Invalid JSON payload passed to %s", __PRETTY_FUNCTION__);
        completionHandler(BugsnagApiClientDeliveryStatusUndeliverable, nil);
        return;
    }
    
    NSError *error = nil;
    NSData *data = [BSGJSONSerialization dataWithJSONObject:payload options:0 error:&error];
    if (!data) {
        bsg_log_err(@"Error: Could not encode JSON payload passed to %s", __PRETTY_FUNCTION__);
        completionHandler(BugsnagApiClientDeliveryStatusUndeliverable, error);
        return;
    }
    
    NSMutableDictionary<BugsnagHTTPHeaderName, NSString *> *mutableHeaders = [headers mutableCopy];
    mutableHeaders[BugsnagHTTPHeaderNameIntegrity] = [NSString stringWithFormat:@"sha1 %@", [BugsnagApiClient SHA1HashStringWithData:data]];
    
    NSMutableURLRequest *request = [self prepareRequest:url headers:mutableHeaders];
    bsg_log_debug(@"Sending %lu byte payload to %@", (unsigned long)data.length, url);
    
    [[self.session uploadTaskWithRequest:request fromData:data completionHandler:^(__attribute__((unused)) NSData *responseData,
                                                                                   NSURLResponse *response, NSError *connectionError) {
        if (![response isKindOfClass:[NSHTTPURLResponse class]]) {
            bsg_log_debug(@"Request to %@ completed with error %@", url, error);
            completionHandler(BugsnagApiClientDeliveryStatusFailed, connectionError ?:
                              [NSError errorWithDomain:@"BugsnagApiClientErrorDomain" code:0 userInfo:@{
                                  NSLocalizedDescriptionKey: @"Request failed: no response was received",
                                  NSURLErrorFailingURLErrorKey: url }]);
            return;
        }
        
        NSInteger statusCode = ((NSHTTPURLResponse *)response).statusCode;
        bsg_log_debug(@"Request to %@ completed with status code %ld", url, (long)statusCode);
        
        if (statusCode / 100 == 2) {
            completionHandler(BugsnagApiClientDeliveryStatusDelivered, nil);
            return;
        }
        
        connectionError = [NSError errorWithDomain:@"BugsnagApiClientErrorDomain" code:1 userInfo:@{
            NSLocalizedDescriptionKey: [NSString stringWithFormat:@"Request failed: unacceptable status code %ld (%@)",
                                        (long)statusCode, [NSHTTPURLResponse localizedStringForStatusCode:statusCode]],
            NSURLErrorFailingURLErrorKey: url }];
        
        bsg_log_debug(@"Response headers: %@", ((NSHTTPURLResponse *)response).allHeaderFields);
        bsg_log_debug(@"Response body: %.*s", (int)data.length, (const char *)data.bytes);
        
        if (statusCode / 100 == 4 &&
            statusCode != HTTPStatusCodePaymentRequired &&
            statusCode != HTTPStatusCodeProxyAuthenticationRequired &&
            statusCode != HTTPStatusCodeClientTimeout &&
            statusCode != HTTPStatusCodeTooManyRequests) {
            completionHandler(BugsnagApiClientDeliveryStatusUndeliverable, connectionError);
            return;
        }
        
        completionHandler(BugsnagApiClientDeliveryStatusFailed, connectionError);
    }] resume];
}

- (NSMutableURLRequest *)prepareRequest:(NSURL *)url
                                headers:(NSDictionary *)headers {
    NSMutableURLRequest *request = [NSMutableURLRequest
            requestWithURL:url
               cachePolicy:NSURLRequestReloadIgnoringLocalCacheData
           timeoutInterval:15];
    request.HTTPMethod = @"POST";
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];

    for (NSString *key in [headers allKeys]) {
        [request setValue:headers[key] forHTTPHeaderField:key];
    }
    return request;
}

+ (NSString *)SHA1HashStringWithData:(NSData *)data {
    if (!data) {
        return nil;
    }
    unsigned char md[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1(data.bytes, (CC_LONG)data.length, md);
    return [NSString stringWithFormat:@"%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x%02x",
            md[0], md[1], md[2], md[3], md[4],
            md[5], md[6], md[7], md[8], md[9],
            md[10], md[11], md[12], md[13], md[14],
            md[15], md[16], md[17], md[18], md[19]];
}

- (void)dealloc {
    [self.sendQueue cancelAllOperations];
}

@end
