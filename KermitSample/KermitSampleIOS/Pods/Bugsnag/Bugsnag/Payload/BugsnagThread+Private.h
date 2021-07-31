//
//  BugsnagThread+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 23/11/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagThread.h>

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagThread ()

- (instancetype)initWithId:(nullable NSString *)identifier
                      name:(nullable NSString *)name
      errorReportingThread:(BOOL)errorReportingThread
                      type:(BSGThreadType)type
                stacktrace:(NSArray<BugsnagStackframe *> *)stacktrace;

- (instancetype)initWithThread:(NSDictionary *)thread binaryImages:(NSArray *)binaryImages;

+ (NSArray<BugsnagThread *> *)allThreads:(BOOL)allThreads callStackReturnAddresses:(NSArray<NSNumber *> *)callStackReturnAddresses;

+ (instancetype)threadFromJson:(NSDictionary *)json;

@property (readonly, nullable, nonatomic) NSString *crashInfoMessage;

@property (readwrite, nonatomic) BOOL errorReportingThread;

+ (NSDictionary *)enhanceThreadInfo:(NSDictionary *)thread
                              depth:(NSUInteger)depth
                          errorType:(nullable NSString *)errorType;

+ (nullable instancetype)mainThread;

+ (NSMutableArray *)serializeThreads:(nullable NSArray<BugsnagThread *> *)threads;

+ (NSMutableArray<BugsnagThread *> *)threadsFromArray:(NSArray *)threads
                                         binaryImages:(NSArray *)binaryImages
                                                depth:(NSUInteger)depth
                                            errorType:(nullable NSString *)errorType;

- (NSDictionary *)toDictionary;

@end

BSGThreadType BSGParseThreadType(NSString *type);

NSString *BSGSerializeThreadType(BSGThreadType type);

NS_ASSUME_NONNULL_END
