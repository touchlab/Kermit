//
//  BugsnagHandledState.m
//  Bugsnag
//
//  Created by Jamie Lynch on 21/09/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import "BugsnagHandledState.h"
#import "BugsnagKeys.h"

BSGSeverity BSGParseSeverity(NSString *severity) {
    if ([severity isEqualToString:BSGKeyInfo])
        return BSGSeverityInfo;
    else if ([severity isEqualToString:BSGKeyWarning])
        return BSGSeverityWarning;
    return BSGSeverityError;
}

NSString *BSGFormatSeverity(BSGSeverity severity) {
    switch (severity) {
    case BSGSeverityError:
        return BSGKeyError;
    case BSGSeverityInfo:
        return BSGKeyInfo;
    case BSGSeverityWarning:
        return BSGKeyWarning;
    }
}

static NSString *const kUnhandled = @"unhandled";
static NSString *const kUnhandledOverridden = @"unhandledOverridden";
static NSString *const kSeverityReasonType = @"severityReasonType";
static NSString *const kOriginalSeverity = @"originalSeverity";
static NSString *const kCurrentSeverity = @"currentSeverity";
static NSString *const kAttrValue = @"attrValue";
static NSString *const kAttrKey = @"attrKey";

static NSString *const kAppHang = @"appHang";
static NSString *const kUnhandledException = @"unhandledException";
static NSString *const kSignal = @"signal";
static NSString *const kPromiseRejection = @"unhandledPromiseRejection";
static NSString *const kHandledError = @"handledError";
static NSString *const kLikelyOutOfMemory = @"outOfMemory";
static NSString *const kLogGenerated = @"log";
static NSString *const kHandledException = @"handledException";
static NSString *const kUserSpecifiedSeverity = @"userSpecifiedSeverity";
static NSString *const kUserCallbackSetSeverity = @"userCallbackSetSeverity";

@implementation BugsnagHandledState

+ (instancetype)handledStateFromJson:(NSDictionary *)json {
    BOOL unhandled = [json[BSGKeyUnhandled] boolValue];
    NSDictionary *severityReason = json[BSGKeySeverityReason];
    BOOL unhandledOverridden = [severityReason[BSGKeyUnhandledOverridden] boolValue];
    BSGSeverity severity = BSGParseSeverity(json[BSGKeySeverity]);

    NSString *attrValue = nil;
    NSDictionary *attrs = severityReason[BSGKeyAttributes];

    if (attrs != nil && [attrs count] == 1) { // only 1 attrValue is ever present
        attrValue = [attrs allValues][0];
    }
    SeverityReasonType reason = [BugsnagHandledState severityReasonFromString:severityReason[BSGKeyType]];
    return [[BugsnagHandledState alloc] initWithSeverityReason:reason
                                                      severity:severity
                                                     unhandled:unhandled
                                           unhandledOverridden:unhandledOverridden
                                                     attrValue:attrValue];
}

+ (instancetype)handledStateWithSeverityReason:
    (SeverityReasonType)severityReason {
    return [self handledStateWithSeverityReason:severityReason
                                       severity:BSGSeverityWarning
                                      attrValue:nil];
}

+ (instancetype)handledStateWithSeverityReason:
                    (SeverityReasonType)severityReason
                                      severity:(BSGSeverity)severity
                                     attrValue:(NSString *)attrValue {
    BOOL unhandled = NO;
    BOOL unhandledOverridden = NO;

    switch (severityReason) {
    case PromiseRejection:
        severity = BSGSeverityError;
        unhandled = YES;
        break;
    case Signal:
        severity = BSGSeverityError;
        unhandled = YES;
        break;
    case HandledError:
        severity = BSGSeverityWarning;
        break;
    case HandledException:
        severity = BSGSeverityWarning;
        break;
    case LogMessage:
    case UserSpecifiedSeverity:
    case UserCallbackSetSeverity:
        break;
    case LikelyOutOfMemory:
    case UnhandledException:
        severity = BSGSeverityError;
        unhandled = YES;
        break;
    case AppHang:
        severity = BSGSeverityError;
        unhandled = NO;
        break;
    }

    return [[BugsnagHandledState alloc] initWithSeverityReason:severityReason
                                                      severity:severity
                                                     unhandled:unhandled
                                           unhandledOverridden:unhandledOverridden
                                                     attrValue:attrValue];
}

- (instancetype)initWithSeverityReason:(SeverityReasonType)severityReason
                              severity:(BSGSeverity)severity
                             unhandled:(BOOL)unhandled
                   unhandledOverridden:(BOOL)unhandledOverridden
                             attrValue:(NSString *)attrValue {
    if ((self = [super init])) {
        _severityReasonType = severityReason;
        _currentSeverity = severity;
        _originalSeverity = severity;
        _unhandled = unhandled;
        _unhandledOverridden = unhandledOverridden;

        if (severityReason == Signal) {
            _attrValue = attrValue;
            _attrKey = @"signalType";
        } else if (severityReason == LogMessage) {
            _attrValue = attrValue;
            _attrKey = @"level";
        }
    }
    return self;
}

- (instancetype)initWithDictionary:(NSDictionary *)dict {
    if ((self = [super init])) {
        _unhandled = [dict[kUnhandled] boolValue];
        _severityReasonType = [BugsnagHandledState
            severityReasonFromString:dict[kSeverityReasonType]];
        _originalSeverity = BSGParseSeverity(dict[kOriginalSeverity]);
        _currentSeverity = BSGParseSeverity(dict[kCurrentSeverity]);
        _attrKey = dict[kAttrKey];
        _attrValue = dict[kAttrValue];
    }
    return self;
}

- (SeverityReasonType)calculateSeverityReasonType {
    return self.originalSeverity == self.currentSeverity ? self.severityReasonType
                                                 : UserCallbackSetSeverity;
}

+ (NSString *)stringFromSeverityReason:(SeverityReasonType)severityReason {
    switch (severityReason) {
    case Signal:
        return kSignal;
    case HandledError:
        return kHandledError;
    case HandledException:
        return kHandledException;
    case UserCallbackSetSeverity:
        return kUserCallbackSetSeverity;
    case PromiseRejection:
        return kPromiseRejection;
    case UserSpecifiedSeverity:
        return kUserSpecifiedSeverity;
    case LogMessage:
        return kLogGenerated;
    case UnhandledException:
        return kUnhandledException;
    case LikelyOutOfMemory:
        return kLikelyOutOfMemory;
    case AppHang:
        return kAppHang;
    }
}

+ (SeverityReasonType)severityReasonFromString:(NSString *)string {
    if ([kUnhandledException isEqualToString:string]) {
        return UnhandledException;
    } else if ([kSignal isEqualToString:string]) {
        return Signal;
    } else if ([kLogGenerated isEqualToString:string]) {
        return LogMessage;
    } else if ([kHandledError isEqualToString:string]) {
        return HandledError;
    } else if ([kHandledException isEqualToString:string]) {
        return HandledException;
    } else if ([kUserSpecifiedSeverity isEqualToString:string]) {
        return UserSpecifiedSeverity;
    } else if ([kUserCallbackSetSeverity isEqualToString:string]) {
        return UserCallbackSetSeverity;
    } else if ([kPromiseRejection isEqualToString:string]) {
        return PromiseRejection;
    } else if ([kLikelyOutOfMemory isEqualToString:string]) {
        return LikelyOutOfMemory;
    } else if ([kAppHang isEqualToString:string]) {
        return AppHang;
    } else {
        return UnhandledException;
    }
}

- (NSDictionary *)toJson {
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[kUnhandled] = @(self.unhandled);
    if(self.unhandledOverridden) {
        dict[kUnhandledOverridden] = @(self.unhandledOverridden);
    }
    dict[kSeverityReasonType] =
        [BugsnagHandledState stringFromSeverityReason:self.severityReasonType];
    dict[kOriginalSeverity] = BSGFormatSeverity(self.originalSeverity);
    dict[kCurrentSeverity] = BSGFormatSeverity(self.currentSeverity);
    dict[kAttrKey] = self.attrKey;
    dict[kAttrValue] = self.attrValue;
    return dict;
}

@end
