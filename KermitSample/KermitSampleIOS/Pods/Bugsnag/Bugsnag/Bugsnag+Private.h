//
//  Bugsnag+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "Bugsnag.h"

NS_ASSUME_NONNULL_BEGIN

@interface Bugsnag ()

#pragma mark Properties

@property (class, readonly, nonatomic) BOOL bugsnagStarted;

@property (class, readonly, nonatomic) BugsnagClient *client;

/// Will be nil until +startWithApiKey: or +startWithConfiguration: has been called.
@property (class, readonly, nullable, nonatomic) BugsnagConfiguration *configuration;

#pragma mark Methods

+ (void)addRuntimeVersionInfo:(NSString *)info withKey:(NSString *)key; // Used in BugsnagReactNative

+ (void)notifyInternal:(BugsnagEvent *)event block:(BOOL (^)(BugsnagEvent *))block; // Used in BugsnagReactNative

+ (void)purge;

+ (void)removeOnBreadcrumbBlock:(BugsnagOnBreadcrumbBlock)block;

+ (void)updateCodeBundleId:(NSString *)codeBundleId; // Used in BugsnagReactNative

@end

NS_ASSUME_NONNULL_END
