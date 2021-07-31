//
//  BugsnagConfiguration.h
//
//  Created by Conrad Irwin on 2014-10-01.
//
//  Copyright (c) 2014 Bugsnag, Inc. All rights reserved.
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

#import <Foundation/Foundation.h>

#import <Bugsnag/BSG_KSCrashReportWriter.h>
#import <Bugsnag/BugsnagBreadcrumb.h>
#import <Bugsnag/BugsnagEvent.h>
#import <Bugsnag/BugsnagMetadata.h>
#import <Bugsnag/BugsnagMetadataStore.h>
#import <Bugsnag/BugsnagPlugin.h>

@class BugsnagUser;
@class BugsnagEndpointConfiguration;
@class BugsnagErrorTypes;

NS_ASSUME_NONNULL_BEGIN

/**
 * Controls whether Bugsnag should capture and serialize the state of all threads at the time
 * of an error.
 */
typedef NS_ENUM(NSInteger, BSGThreadSendPolicy) {

    /**
     * Threads should be captured for all events.
     */
    BSGThreadSendPolicyAlways = 0,

    /**
     * Threads should be captured for unhandled events only.
     */
    BSGThreadSendPolicyUnhandledOnly = 1,

    /**
     * Threads should never be captured.
     */
    BSGThreadSendPolicyNever = 2
};

/**
 * Setting `BugsnagConfiguration.appHangThresholdMillis` to this value disables the reporting of
 * app hangs that ended before the app was terminated.
 */
extern const NSUInteger BugsnagAppHangThresholdFatalOnly;

/**
 *  A configuration block for modifying an error report
 *
 *  @param event the error report to be modified
 */
typedef BOOL (^BugsnagOnErrorBlock)(BugsnagEvent *_Nonnull event);

/**
 *  A handler for modifying data before sending it to Bugsnag.
 *
 * onSendErrorBlocks will be invoked on a dedicated
 * background queue, which will be different from the queue where the block was originally added.
 *
 *  @param event The event report.
 *
 *  @return YES if the event should be sent
 */
typedef BOOL (^BugsnagOnSendErrorBlock)(BugsnagEvent *_Nonnull event);

/**
 *  A configuration block for modifying a captured breadcrumb
 *
 *  @param breadcrumb The breadcrumb
 */
typedef BOOL (^BugsnagOnBreadcrumbBlock)(BugsnagBreadcrumb *_Nonnull breadcrumb);

/**
 * A configuration block for modifying a session. Intended for internal usage only.
 *
 * @param session The session about to be delivered
 */
typedef BOOL (^BugsnagOnSessionBlock)(BugsnagSession *_Nonnull session);

// =============================================================================
// MARK: - BugsnagConfiguration
// =============================================================================

/**
 * Contains user-provided configuration, including API key and endpoints.
 */
@interface BugsnagConfiguration : NSObject <BugsnagMetadataStore>

/**
 * Create a new configuration from the main bundle's infoDictionary, using keys nested under
 * the "bugsnag" key.
 *
 * @return a BugsnagConfiguration containing the options set in the plist file
 */
+ (instancetype)loadConfig;

/**
 * Initializes a new configuration object with the provided API key.
 */
- (instancetype)initWithApiKey:(nullable NSString *)apiKey NS_DESIGNATED_INITIALIZER NS_SWIFT_NAME(init(_:));

/**
 * Required declaration to suppress a superclass designated-initializer error
 */
- (instancetype)init NS_UNAVAILABLE NS_SWIFT_UNAVAILABLE("Use initWithApiKey:");

// -----------------------------------------------------------------------------
// MARK: - Properties
// -----------------------------------------------------------------------------

/**
 *  The API key of a Bugsnag project
 */
@property (copy, nonatomic) NSString *apiKey;

/**
 *  The release stage of the application, such as production, development, beta
 *  et cetera
 */
@property (copy, nullable, nonatomic) NSString *releaseStage;

/**
 *  Release stages which are allowed to notify Bugsnag
 */
@property (copy, nullable, nonatomic) NSSet<NSString *> *enabledReleaseStages;

/**
 * Sets which values should be removed from any Metadata objects before
 * sending them to Bugsnag. Use this if you want to ensure you don't send
 * sensitive data such as passwords, and credit card numbers to our
 * servers. Any keys which contain a match will be filtered.
 *
 * By default, redactedKeys is set to ["password"]. Both string literals and regex
 * values can be supplied to this property.
 */
@property (copy, nullable, nonatomic) NSSet<id> *redactedKeys;

/**
 * A set of strings and / or NSRegularExpression objects that determine which errors should
 * be discarded based on their `errorClass`.
 *
 * Comparisons are case sensitive.
 *
 * OnError / OnSendError blocks will not be called for discarded errors.
 *
 * Some examples of errorClass are: Objective-C exception names like "NSRangeException",
 * signal names like "SIGABRT", mach exception names like "EXC_BREAKPOINT", and Swift
 * error names like "Fatal error".
 */
@property (copy, nullable, nonatomic) NSSet<id> *discardClasses;

/**
 *  A general summary of what was occuring in the application
 */
@property (copy, nullable, atomic) NSString *context;

/**
 *  The version of the application
 */
@property (copy, nullable, nonatomic) NSString *appVersion;

/**
 *  The URL session used to send requests to Bugsnag.
 */
@property (readwrite, strong, nonnull, nonatomic) NSURLSession *session;

/**
 * Controls whether Bugsnag should capture and serialize the state of all threads at the time
 * of an error.
 *
 * By default sendThreads is set to BSGThreadSendPolicyAlways. This can be set to
 * BSGThreadSendPolicyNever to disable or BSGThreadSendPolicyUnhandledOnly
 * to only do so for unhandled errors.
 */
@property (nonatomic) BSGThreadSendPolicy sendThreads;

/**
 *  Optional handler invoked when an error or crash occurs
 */
@property (nullable, nonatomic) void (* onCrashHandler)(const BSG_KSCrashReportWriter *);

/**
 *  YES if uncaught exceptions and other crashes should be reported automatically
 */
@property (nonatomic) BOOL autoDetectErrors;

/**
 * The minimum number of milliseconds of main thread unresponsiveness that will trigger the
 * detection and reporting of an app hang.
 *
 * Set to `BugsnagAppHangThresholdFatalOnly` to disable reporting of app hangs that did not
 * end with the app being force quit by the user or terminated by the system watchdog.
 *
 * By default this is `BugsnagAppHangThresholdFatalOnly`, and can be set to a minimum of 250
 * milliseconds.
 */
@property (nonatomic) NSUInteger appHangThresholdMillis;

/**
 * Determines whether app sessions should be tracked automatically. By default this value is true.
 * If this value is updated after +[Bugsnag start] is called, only subsequent automatic sessions
 * will be captured.
 */
@property (nonatomic) BOOL autoTrackSessions;

/**
 * The amount of time (in milliseconds) after starting Bugsnag that should be considered part of
 * the app's launch.
 *
 * Events that occur during app launch will have the `BugsnagAppWithState.isLaunching` property
 * set to true.
 *
 * By default this value is 5000 milliseconds.
 *
 * Setting this to `0` will cause Bugsnag to consider the app to be launching until
 * `+[Bugsnag markLaunchCompleted]` or `-[BugsnagClient markLaunchCompleted]` has been called.
 */
@property (nonatomic) NSUInteger launchDurationMillis;

/**
 * Determines whether launch crashes should be sent synchronously during `+[Bugsnag start]`.
 *
 * If true and the previous run terminated due to a crash during app launch, `+[Bugsnag start]`
 * will block the calling thread for up to 2 seconds while the crash report is sent.
 *
 * By default this value is true.
 */
@property (nonatomic) BOOL sendLaunchCrashesSynchronously;

/**
 * The types of breadcrumbs which will be captured. By default, this is all types.
 */
@property (nonatomic) BSGEnabledBreadcrumbType enabledBreadcrumbTypes;

/**
 * The app's bundleVersion, set from the CFBundleVersion.  Equivalent to `versionCode` on Android.
 */
@property (copy, nullable, nonatomic) NSString *bundleVersion;

@property (copy, nullable, nonatomic) NSString *appType;

/**
 * Sets the maximum number of events which will be stored. Once the threshold is reached,
 * the oldest events will be deleted.
 *
 * By default, 32 events are persisted.
 */
@property (nonatomic) NSUInteger maxPersistedEvents;

/**
 * Sets the maximum number of sessions which will be stored. Once the threshold is reached,
 * the oldest sessions will be deleted.
 *
 * By default, 128 sessions are persisted.
 */
@property (nonatomic) NSUInteger maxPersistedSessions;

/**
 * Sets the maximum number of breadcrumbs which will be stored. Once the threshold is reached,
 * the oldest breadcrumbs will be deleted.
 *
 * By default, 25 breadcrumbs are stored: this can be amended up to a maximum of 100.
 */
@property (nonatomic) NSUInteger maxBreadcrumbs;

/**
 * Whether User information should be persisted to disk between application runs.
 * Defaults to True.
 */
@property (nonatomic) BOOL persistUser;

// -----------------------------------------------------------------------------
// MARK: - Methods
// -----------------------------------------------------------------------------

/**
 * A class defining the types of error that are reported. By default,
 * all properties are true.
 */
@property (strong, nonatomic) BugsnagErrorTypes *enabledErrorTypes;

/**
 * Set the endpoints to send data to. By default we'll send error reports to
 * https://notify.bugsnag.com, and sessions to https://sessions.bugsnag.com, but you can
 * override this if you are using Bugsnag Enterprise to point to your own Bugsnag endpoint.
 *
 * Please note that it is recommended that you set both endpoints. If the notify endpoint is
 * missing, an assertion will be thrown. If the session endpoint is missing, a warning will be
 * logged and sessions will not be sent automatically.
 */
@property (copy, nonatomic) BugsnagEndpointConfiguration *endpoints;

// =============================================================================
// MARK: - User
// =============================================================================

/**
 * The current user
 */
@property(readonly, retain, nonnull, nonatomic) BugsnagUser *user;

/**
 *  Set user metadata
 *
 *  @param userId ID of the user
 *  @param name   Name of the user
 *  @param email  Email address of the user
 */
- (void)setUser:(NSString *_Nullable)userId
      withEmail:(NSString *_Nullable)email
        andName:(NSString *_Nullable)name;

// =============================================================================
// MARK: - onSession
// =============================================================================

/**
 *  Add a callback to be invoked before a session is sent to Bugsnag.
 *
 *  @param block A block which can modify the session
 */
- (void)addOnSessionBlock:(BugsnagOnSessionBlock _Nonnull)block
    NS_SWIFT_NAME(addOnSession(block:));

/**
 * Remove a callback that would be invoked before a session is sent to Bugsnag.
 *
 * @param block The block to be removed.
 */
- (void)removeOnSessionBlock:(BugsnagOnSessionBlock _Nonnull)block
    NS_SWIFT_NAME(removeOnSession(block:));

// =============================================================================
// MARK: - onSend
// =============================================================================

/**
 *  Add a callback to be invoked before a report is sent to Bugsnag, to
 *  change the report contents as needed
 *
 *  @param block A block which returns YES if the report should be sent
 */
- (void)addOnSendErrorBlock:(BugsnagOnSendErrorBlock _Nonnull)block
    NS_SWIFT_NAME(addOnSendError(block:));

/**
 * Remove the callback that would be invoked before an event is sent.
 *
 * @param block The block to be removed.
 */
- (void)removeOnSendErrorBlock:(BugsnagOnSendErrorBlock _Nonnull)block
    NS_SWIFT_NAME(removeOnSendError(block:));

// =============================================================================
// MARK: - onBreadcrumb
// =============================================================================

/**
 *  Add a callback to be invoked when a breadcrumb is captured by Bugsnag, to
 *  change the breadcrumb contents as needed
 *
 *  @param block A block which returns YES if the breadcrumb should be captured
 */
- (void)addOnBreadcrumbBlock:(BugsnagOnBreadcrumbBlock _Nonnull)block
    NS_SWIFT_NAME(addOnBreadcrumb(block:));

/**
 * Remove the callback that would be invoked when a breadcrumb is captured.
 *
 * @param block The block to be removed.
 */
- (void)removeOnBreadcrumbBlock:(BugsnagOnBreadcrumbBlock _Nonnull)block
    NS_SWIFT_NAME(removeOnBreadcrumb(block:));

- (void)addPlugin:(id<BugsnagPlugin> _Nonnull)plugin;

@end

NS_ASSUME_NONNULL_END
