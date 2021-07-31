//
//  BugsnagSystemInfo.h
//  Bugsnag
//
//  Created by Karl Stenerud on 21.09.20.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BugsnagConfiguration.h"
#import "BugsnagKeys.h"

#define SYSTEMSTATE_KEY_APP @"app"
#define SYSTEMSTATE_KEY_DEVICE @"device"

#define SYSTEMSTATE_APP_WAS_TERMINATED @"wasTerminated"
#define SYSTEMSTATE_APP_IS_ACTIVE @"isActive"
#define SYSTEMSTATE_APP_IS_IN_FOREGROUND @"inForeground"
#define SYSTEMSTATE_APP_IS_LAUNCHING BSGKeyIsLaunching
#define SYSTEMSTATE_APP_VERSION @"version"
#define SYSTEMSTATE_APP_BUNDLE_VERSION @"bundleVersion"
#define SYSTEMSTATE_APP_DEBUGGER_IS_ACTIVE @"debuggerIsActive"

#define SYSTEMSTATE_DEVICE_BOOT_TIME @"bootTime"

#define PLATFORM_WORD_SIZE sizeof(void*)*8

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagSystemState : NSObject

@property(readonly,nonatomic) NSDictionary *lastLaunchState;
@property(readonly,atomic) NSDictionary *currentLaunchState;

- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithConfiguration:(BugsnagConfiguration *)config;

- (void)setCodeBundleID:(NSString*)codeBundleID;

@property (nonatomic) NSUInteger consecutiveLaunchCrashes;

- (void)markLaunchCompleted;

/**
 * Purge all stored system state.
 */
- (void)purge;

@end

NS_ASSUME_NONNULL_END
