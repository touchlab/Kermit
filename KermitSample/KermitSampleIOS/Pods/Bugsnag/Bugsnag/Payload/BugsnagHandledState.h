//
//  BugsnagHandledState.h
//  Bugsnag
//
//  Created by Jamie Lynch on 21/09/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import "BugsnagEvent.h"
#import <Foundation/Foundation.h>

typedef NS_ENUM(NSUInteger, SeverityReasonType) {
    UnhandledException,
    Signal,
    HandledError,
    HandledException,
    UserSpecifiedSeverity,
    UserCallbackSetSeverity,
    PromiseRejection,
    LogMessage,
    LikelyOutOfMemory,
    AppHang,
};

/**
 *  Convert a string to a severity value
 *
 *  @param severity Intended severity value, such as info, warning, or error
 *
 *  @return converted severity level or BSGSeverityError if no conversion is
 * found
 */
BSGSeverity BSGParseSeverity(NSString *severity);

/**
 *  Serialize a severity for JSON payloads
 *
 *  @param severity a severity
 *
 *  @return the equivalent string value
 */
NSString *BSGFormatSeverity(BSGSeverity severity);

@interface BugsnagHandledState : NSObject

@property(nonatomic) BOOL unhandled;
@property(nonatomic) BOOL unhandledOverridden;
@property(nonatomic, readonly) SeverityReasonType severityReasonType;
@property(nonatomic, readonly) BSGSeverity originalSeverity;
@property(nonatomic) BSGSeverity currentSeverity;
@property(nonatomic, readonly) SeverityReasonType calculateSeverityReasonType;
@property(nonatomic, readonly, strong) NSString *attrValue;
@property(nonatomic, readonly, strong) NSString *attrKey;

+ (NSString *)stringFromSeverityReason:(SeverityReasonType)severityReason;
+ (SeverityReasonType)severityReasonFromString:(NSString *)string;

+ (instancetype)handledStateWithSeverityReason:
    (SeverityReasonType)severityReason;

+ (instancetype)handledStateFromJson:(NSDictionary *)json;

+ (instancetype)handledStateWithSeverityReason:
                    (SeverityReasonType)severityReason
                                      severity:(BSGSeverity)severity
                                     attrValue:(NSString *)attrValue;

- (instancetype)initWithSeverityReason:(SeverityReasonType)severityReason
                              severity:(BSGSeverity)severity
                             unhandled:(BOOL)unhandled
                   unhandledOverridden:(BOOL)unhandledOverridden
                             attrValue:(NSString *)attrValue;

- (NSDictionary *)toJson;

- (instancetype)initWithDictionary:(NSDictionary *)dict;

@end
