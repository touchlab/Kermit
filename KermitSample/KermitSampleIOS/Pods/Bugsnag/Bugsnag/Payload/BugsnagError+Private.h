//
//  BugsnagError+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 23/11/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagError.h>

NS_ASSUME_NONNULL_BEGIN

@class BugsnagThread;

@interface BugsnagError ()

- (instancetype)initWithKSCrashReport:(NSDictionary *)event stacktrace:(NSArray<BugsnagStackframe *> *)stacktrace;

- (instancetype)initWithErrorClass:(NSString *)errorClass
                      errorMessage:(nullable NSString *)errorMessage
                         errorType:(BSGErrorType)errorType
                        stacktrace:(nullable NSArray<BugsnagStackframe *> *)stacktrace;

+ (BugsnagError *)errorFromJson:(NSDictionary *)json;

/// The string representation of the BSGErrorType
@property (copy, nonatomic) NSString *typeString;

/// Parses the `__crash_info` message and updates the `errorClass` and `errorMessage` as appropriate.
- (void)updateWithCrashInfoMessage:(NSString *)crashInfoMessage;

- (NSDictionary *)toDictionary;

@end

NSString *BSGParseErrorClass(NSDictionary *error, NSString *errorType);

NSString * _Nullable BSGParseErrorMessage(NSDictionary *report, NSDictionary *error, NSString *errorType);

BSGErrorType BSGParseErrorType(NSString *errorType);

NSString *BSGSerializeErrorType(BSGErrorType errorType);

NS_ASSUME_NONNULL_END
