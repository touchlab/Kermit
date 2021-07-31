//
//  BSG_KSCrash.m
//
//  Created by Karl Stenerud on 2012-01-28.
//
//  Copyright (c) 2012 Karl Stenerud. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall remain in place
// in this source code.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

#import "BugsnagPlatformConditional.h"

#import <execinfo.h>
#import "BSG_KSCrashAdvanced.h"

#import "BSG_KSCrashC.h"
#import "BSG_KSJSONCodecObjC.h"
#import "NSError+BSG_SimpleConstructor.h"

//#define BSG_KSLogger_LocalLevel TRACE
#import "BSG_KSLogger.h"
#import "BugsnagThread+Private.h"
#import "BSGJSONSerialization.h"
#import "BSGSerialization.h"
#import "Bugsnag.h"
#import "BugsnagCollections.h"
#import "BSG_KSCrashIdentifier.h"
#import "BSG_KSCrashReportFields.h"

#if BSG_HAS_UIKIT
#import "BSGUIKit.h"
#endif
#if TARGET_OS_OSX
#import "BSGAppKit.h"
#endif

// ============================================================================
#pragma mark - Constants -
// ============================================================================

#define BSG_kCrashStateFilenameSuffix "-CrashState.json"

// ============================================================================
#pragma mark - Globals -
// ============================================================================

@interface BSG_KSCrash ()

@property(nonatomic, readwrite, retain) NSString *nextCrashID;

// Mirrored from BSG_KSCrashAdvanced.h to provide ivars
@property(nonatomic, readwrite, retain) NSString *logFilePath;
@property(nonatomic, readwrite, assign) BSGReportCallback onCrash;
@property(nonatomic, readwrite, assign) bool printTraceToStdout;
@property(nonatomic, readwrite, assign) int maxStoredReports;

@end

@implementation BSG_KSCrash

// ============================================================================
#pragma mark - Properties -
// ============================================================================

@synthesize userInfo = _userInfo;
@synthesize printTraceToStdout = _printTraceToStdout;
@synthesize onCrash = _onCrash;
@synthesize logFilePath = _logFilePath;
@synthesize nextCrashID = _nextCrashID;
@synthesize introspectMemory = _introspectMemory;
@synthesize maxStoredReports = _maxStoredReports;
@synthesize reportWhenDebuggerIsAttached = _reportWhenDebuggerIsAttached;
@synthesize threadTracingEnabled = _threadTracingEnabled;

// ============================================================================
#pragma mark - Lifecycle -
// ============================================================================

+ (BSG_KSCrash *)sharedInstance {
    static id sharedInstance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

- (instancetype)init {
    if ((self = [super init])) {
        self.nextCrashID = [NSUUID UUID].UUIDString;
        self.introspectMemory = YES;
        self.maxStoredReports = 5;
        self.reportWhenDebuggerIsAttached = NO;
    }
    return self;
}

// ============================================================================
#pragma mark - API -
// ============================================================================

- (void)setUserInfo:(NSDictionary *)userInfo {
    NSError *error = nil;
    NSData *userInfoJSON = nil;
    if (userInfo != nil) {
        userInfoJSON = [self
            nullTerminated:[BSG_KSJSONCodec encode:userInfo
                                           options:BSG_KSJSONEncodeOptionSorted
                                             error:&error]];
        if (error != NULL) {
            BSG_KSLOG_ERROR(@"Could not serialize user info: %@", error);
            return;
        }
    }

    _userInfo = userInfo;
    bsg_kscrash_setUserInfoJSON([userInfoJSON bytes]);
}

- (void)setPrintTraceToStdout:(bool)printTraceToStdout {
    _printTraceToStdout = printTraceToStdout;
    bsg_kscrash_setPrintTraceToStdout(printTraceToStdout);
}

- (void)setOnCrash:(BSGReportCallback)onCrash {
    _onCrash = onCrash;
    bsg_kscrash_setCrashNotifyCallback(onCrash);
}

- (void)setIntrospectMemory:(bool)introspectMemory {
    _introspectMemory = introspectMemory;
    bsg_kscrash_setIntrospectMemory(introspectMemory);
}

- (void)setReportWhenDebuggerIsAttached:(BOOL)reportWhenDebuggerIsAttached {
    _reportWhenDebuggerIsAttached = reportWhenDebuggerIsAttached;
    bsg_kscrash_setReportWhenDebuggerIsAttached(reportWhenDebuggerIsAttached);
}

- (void)setThreadTracingEnabled:(BOOL)threadTracingEnabled {
    _threadTracingEnabled = threadTracingEnabled;
    bsg_kscrash_setThreadTracingEnabled(threadTracingEnabled);
}

- (BSG_KSCrashType)install:(BSG_KSCrashType)crashTypes directory:(NSString *)directory {
    bsg_kscrash_generate_report_initialize(directory.fileSystemRepresentation);
    char *crashReportPath = bsg_kscrash_generate_report_path(self.nextCrashID.UTF8String, false);
    char *recrashReportPath = bsg_kscrash_generate_report_path(self.nextCrashID.UTF8String, true);
    NSString *stateFilePrefix = [[NSBundle mainBundle] infoDictionary][@"CFBundleName"]
    /* Not all processes have an Info.plist */ ?: NSProcessInfo.processInfo.processName;
    NSString *stateFilePath = [directory stringByAppendingPathComponent:
                               [stateFilePrefix stringByAppendingString:@BSG_kCrashStateFilenameSuffix]];
    
    bsg_kscrash_setHandlingCrashTypes(crashTypes);
    
    BSG_KSCrashType installedCrashTypes = bsg_kscrash_install(
        crashReportPath, recrashReportPath,
        [stateFilePath UTF8String], [self.nextCrashID UTF8String]);
    
    free(crashReportPath);
    free(recrashReportPath);
    
    NSNotificationCenter *nCenter = [NSNotificationCenter defaultCenter];
#if BSG_HAS_UIKIT
    [nCenter addObserver:self
                selector:@selector(applicationDidBecomeActive)
                    name:UIApplicationDidBecomeActiveNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationWillResignActive)
                    name:UIApplicationWillResignActiveNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationDidEnterBackground)
                    name:UIApplicationDidEnterBackgroundNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationWillEnterForeground)
                    name:UIApplicationWillEnterForegroundNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationWillTerminate)
                    name:UIApplicationWillTerminateNotification
                  object:nil];
#elif TARGET_OS_OSX
    // MacOS "active" serves the same purpose as "foreground" in iOS
    [nCenter addObserver:self
                selector:@selector(applicationDidEnterBackground)
                    name:NSApplicationDidResignActiveNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationWillEnterForeground)
                    name:NSApplicationDidBecomeActiveNotification
                  object:nil];
    [nCenter addObserver:self
                selector:@selector(applicationWillTerminate)
                    name:NSApplicationWillTerminateNotification
                  object:nil];
#endif

    return installedCrashTypes;
}

- (NSDictionary *)captureAppStats {
    BSG_KSCrash_State state = crashContext()->state;
    bsg_kscrashstate_updateDurationStats(&state);
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[@BSG_KSCrashField_ActiveTimeSinceLaunch] = @(state.foregroundDurationSinceLaunch);
    dict[@BSG_KSCrashField_BGTimeSinceLaunch] = @(state.backgroundDurationSinceLaunch);
    dict[@BSG_KSCrashField_AppInFG] = @(state.applicationIsInForeground);
    return dict;
}

// ============================================================================
#pragma mark - Advanced API -
// ============================================================================

#define BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(TYPE, NAME)                        \
    -(TYPE)NAME {                                                              \
        return bsg_kscrashstate_currentState()->NAME;                          \
    }

BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(NSTimeInterval,
                                    foregroundDurationSinceLastCrash)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(NSTimeInterval,
                                    backgroundDurationSinceLastCrash)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(int, launchesSinceLastCrash)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(int, sessionsSinceLastCrash)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(NSTimeInterval, foregroundDurationSinceLaunch)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(NSTimeInterval,
                                    backgroundDurationSinceLaunch)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(int, sessionsSinceLaunch)
BSG_SYNTHESIZE_CRASH_STATE_PROPERTY(BOOL, crashedLastLaunch)

- (BOOL)redirectConsoleLogsToFile:(NSString *)fullPath
                        overwrite:(BOOL)overwrite {
    if (bsg_kslog_setLogFilename([fullPath UTF8String], overwrite)) {
        self.logFilePath = fullPath;
        return YES;
    }
    return NO;
}

// ============================================================================
#pragma mark - Utility -
// ============================================================================


- (NSMutableData *)nullTerminated:(NSData *)data {
    if (data == nil) {
        return NULL;
    }
    NSMutableData *mutable = [NSMutableData dataWithData:data];
    [mutable appendBytes:"\0" length:1];
    return mutable;
}

- (const char *)encodeAsJSONString:(id)object {
    if (object == nil) {
        return NULL;
    }
    NSError *error = nil;
    NSData *jsonData = [BSG_KSJSONCodec encode:object options:0 error:&error];
    if (jsonData == nil || error != nil) {
        BSG_KSLOG_ERROR(@"Error encoding object to JSON: %@", error);
        // we can still record other useful information from the report
        return NULL;
    }
    NSString *jsonString =
    [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    return [jsonString cStringUsingEncoding:NSUTF8StringEncoding];
}

// ============================================================================
#pragma mark - Callbacks -
// ============================================================================

- (void)applicationDidBecomeActive {
    bsg_kscrashstate_notifyAppInForeground(true);
}

- (void)applicationWillResignActive {
    bsg_kscrashstate_notifyAppInForeground(true);
}

- (void)applicationDidEnterBackground {
    bsg_kscrashstate_notifyAppInForeground(false);
}

- (void)applicationWillEnterForeground {
    bsg_kscrashstate_notifyAppInForeground(true);
}

- (void)applicationWillTerminate {
    bsg_kscrashstate_notifyAppTerminate();
}

@end

//! Project version number for BSG_KSCrashFramework.
const double BSG_KSCrashFrameworkVersionNumber = 1.813;

//! Project version string for BSG_KSCrashFramework.
const unsigned char BSG_KSCrashFrameworkVersionString[] = "1.8.13";
