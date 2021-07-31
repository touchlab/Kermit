//
//  BugsnagClient+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 26/11/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagClient.h>

#import "BugsnagMetadata+Private.h" // For BugsnagObserverBlock

@class BSGAppHangDetector;
@class BSGEventUploader;
@class BugsnagAppWithState;
@class BugsnagBreadcrumbs;
@class BugsnagConfiguration;
@class BugsnagCrashSentry;
@class BugsnagDeviceWithState;
@class BugsnagMetadata;
@class BugsnagNotifier;
@class BugsnagPluginClient;
@class BugsnagSessionTracker;
@class BugsnagSystemState;

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagClient ()

#pragma mark Properties

@property (nonatomic) BOOL appDidCrashLastLaunch;

@property (nonatomic) BSGAppHangDetector *appHangDetector;

@property (nullable, nonatomic) BugsnagEvent *appHangEvent;

/// Alters whether error detection should be enabled or not after Bugsnag has been initialized.
/// Intended for internal use only by Unity.
@property (nonatomic) BOOL autoNotify;

@property (nullable, retain, nonatomic) BugsnagBreadcrumbs *breadcrumbs;

@property (nullable, nonatomic) NSString *codeBundleId;

@property (readonly, nonatomic) NSString *configMetadataFile;

@property (nullable, nonatomic) NSDictionary *configMetadataFromLastLaunch;

@property (retain, nonatomic) BugsnagConfiguration *configuration;

@property (strong, nonatomic) BugsnagCrashSentry *crashSentry;

/// The App hang or OOM event that caused the last launch to crash.
@property (nullable, nonatomic) BugsnagEvent *eventFromLastLaunch;

@property (strong, nonatomic) BSGEventUploader *eventUploader;

@property (nonatomic) NSMutableDictionary *extraRuntimeInfo;

#if TARGET_OS_IOS
@property (strong, nonatomic) NSString *lastOrientation;
#endif

@property (strong, nonatomic) BugsnagMetadata *metadata; // Used in BugsnagReactNative

@property (readonly, nonatomic) NSString *metadataFile;

@property (nullable, nonatomic) NSDictionary *metadataFromLastLaunch;

@property (strong, nonatomic) BugsnagNotifier *notifier; // Used in BugsnagReactNative

@property (strong, nonatomic) BugsnagPluginClient *pluginClient;

@property (strong, nonatomic) BugsnagSessionTracker *sessionTracker; // Used in BugsnagReactNative

@property (nonatomic) BOOL started;

/// State related metadata
///
/// Upon change this is automatically persisted to disk, making it available when contructing OOM payloads.
/// Is it also added to KSCrashReports under `user.state` by `BSSerializeDataCrashHandler()`.
///
/// Example contents:
///
/// {
///     "app": {
///         "codeBundleId": "com.example.app",
///         "isLaunching": true
///     },
///     "client": {
///         "context": "MyViewController",
///     },
///     "deviceState": {
///         "batteryLevel": 0.5,
///         "charging": false,
///         "lowMemoryWarning": "2021-01-01T15:29:02.170Z",
///         "orientation": "portrait"
///     },
///     "user": {
///         "id": "abc123",
///         "name": "bob"
///     }
/// }
@property (strong, nonatomic) BugsnagMetadata *state;

@property (strong, nonatomic) NSMutableArray *stateEventBlocks;

@property (readonly, nonatomic) NSString *stateMetadataFile;

@property (nullable, nonatomic) NSDictionary *stateMetadataFromLastLaunch;

@property (strong, nonatomic) BugsnagSystemState *systemState;

@property (nonatomic) BugsnagUser *user;

#pragma mark Methods

- (void)addBreadcrumbWithBlock:(void (^)(BugsnagBreadcrumb *))block;

- (void)addObserverWithBlock:(BugsnagObserverBlock)block; // Used in BugsnagReactNative

- (void)addRuntimeVersionInfo:(NSString *)info withKey:(NSString *)key;

- (NSDictionary *)collectAppWithState; // Used in BugsnagReactNative

- (NSArray *)collectBreadcrumbs; // Used in BugsnagReactNative

- (NSDictionary *)collectDeviceWithState; // Used in BugsnagReactNative

- (NSArray *)collectThreads:(BOOL)unhandled; // Used in BugsnagReactNative

- (BugsnagAppWithState *)generateAppWithState:(NSDictionary *)systemInfo;

- (BugsnagDeviceWithState *)generateDeviceWithState:(NSDictionary *)systemInfo;

- (BugsnagEvent *)generateOutOfMemoryEvent;

/// @return A `BugsnagEvent` if the last run ended with a fatal app hang, `nil` otherwise.
- (nullable BugsnagEvent *)loadFatalAppHangEvent;

- (void)notifyInternal:(BugsnagEvent *)event block:(nullable BugsnagOnErrorBlock)block;

- (void)removeObserverWithBlock:(BugsnagObserverBlock)block; // Used in BugsnagReactNative

- (BOOL)shouldReportOOM;

- (void)start;

- (void)startAppHangDetector;

@end

NS_ASSUME_NONNULL_END
