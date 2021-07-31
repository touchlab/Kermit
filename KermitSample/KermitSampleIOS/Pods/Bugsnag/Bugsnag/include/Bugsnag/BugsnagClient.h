//
//  BugsnagMetaData.m
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

#import <Bugsnag/BugsnagConfiguration.h>
#import <Bugsnag/BugsnagLastRunInfo.h>
#import <Bugsnag/BugsnagMetadata.h>
#import <Bugsnag/BugsnagMetadataStore.h>

@class BugsnagSessionTracker;

/**
 * The BugsnagClient is not intended to be used directly.
 * 
 * Use the static access provided by the Bugsnag class instead.
 */
@interface BugsnagClient : NSObject<BugsnagMetadataStore>

/**
 * Initializes the client with the provided configuration.
 */
- (instancetype _Nonnull)initWithConfiguration:(BugsnagConfiguration *_Nonnull)configuration;

// =============================================================================
// MARK: - Notify
// =============================================================================

/** Send a custom or caught exception to Bugsnag.
 *
 * The exception will be sent to Bugsnag in the background allowing your
 * app to continue running.
 *
 * @param exception  The exception.
 */
- (void)notify:(NSException *_Nonnull)exception;

/**
 *  Send a custom or caught exception to Bugsnag
 *
 *  @param exception The exception
 *  @param block     A block for optionally configuring the error report
 */
- (void)notify:(NSException *_Nonnull)exception
         block:(BugsnagOnErrorBlock _Nullable)block;

/**
 *  Send an error to Bugsnag
 *
 *  @param error The error
 */
- (void)notifyError:(NSError *_Nonnull)error;

/**
 *  Send an error to Bugsnag
 *
 *  @param error The error
 *  @param block A block for optionally configuring the error report
 */
- (void)notifyError:(NSError *_Nonnull)error
              block:(BugsnagOnErrorBlock _Nullable)block;

// =============================================================================
// MARK: - Breadcrumbs
// =============================================================================

/**
 * Leave a "breadcrumb" log message, representing an action that occurred
 * in your app, to aid with debugging.
 *
 * @param message  the log message to leave
 */
- (void)leaveBreadcrumbWithMessage:(NSString *_Nonnull)message;

/**
 *  Leave a "breadcrumb" log message each time a notification with a provided
 *  name is received by the application
 *
 *  @param notificationName name of the notification to capture
 */
- (void)leaveBreadcrumbForNotificationName:(NSString *_Nonnull)notificationName;

/**
 * Leave a "breadcrumb" log message, representing an action that occurred
 * in your app, to aid with debugging, along with additional metadata and
 * a type.
 *
 * @param message The log message to leave.
 * @param metadata Additional metadata included with the breadcrumb.
 * @param type A BSGBreadcrumbTypeValue denoting the type of breadcrumb.
 */
- (void)leaveBreadcrumbWithMessage:(NSString *_Nonnull)message
                          metadata:(NSDictionary *_Nullable)metadata
                           andType:(BSGBreadcrumbType)type
NS_SWIFT_NAME(leaveBreadcrumb(_:metadata:type:));

// =============================================================================
// MARK: - Session
// =============================================================================

/**
 * Starts tracking a new session.
 *
 * By default, sessions are automatically started when the application enters the foreground.
 * If you wish to manually call startSession at
 * the appropriate time in your application instead, the default behaviour can be disabled via
 * autoTrackSessions.
 *
 * Any errors which occur in an active session count towards your application's
 * stability score. You can prevent errors from counting towards your stability
 * score by calling pauseSession and resumeSession at the appropriate
 * time in your application.
 *
 * @see pauseSession:
 * @see resumeSession:
 */
- (void)startSession;

/**
 * Stops tracking a session.
 *
 * When a session is stopped, errors will not count towards your application's
 * stability score. This can be advantageous if you do not wish these calculations to
 * include a certain type of error, for example, a crash in a background service.
 * You should disable automatic session tracking via autoTrackSessions if you call this method.
 *
 * A stopped session can be resumed by calling resumeSession,
 * which will make any subsequent errors count towards your application's
 * stability score. Alternatively, an entirely new session can be created by calling startSession.
 *
 * @see startSession:
 * @see resumeSession:
 */
- (void)pauseSession;

/**
 * Resumes a session which has previously been stopped, or starts a new session if none exists.
 *
 * If a session has already been resumed or started and has not been stopped, calling this
 * method will have no effect. You should disable automatic session tracking via
 * autoTrackSessions if you call this method.
 *
 * It's important to note that sessions are stored in memory for the lifetime of the
 * application process and are not persisted on disk. Therefore calling this method on app
 * startup would start a new session, rather than continuing any previous session.
 *
 * You should call this at the appropriate time in your application when you wish to
 * resume a previously started session. Any subsequent errors which occur in your application
 * will be reported to Bugsnag and will count towards your application's stability score.
 *
 * @see startSession:
 * @see pauseSession:
 *
 * @return true if a previous session was resumed, false if a new session was started.
 */
- (BOOL)resumeSession;

// =============================================================================
// MARK: - onSession
// =============================================================================

/**
* Add a callback that would be invoked before a session is sent to Bugsnag.
*
* @param block The block to be added.
*/
- (void)addOnSessionBlock:(BugsnagOnSessionBlock _Nonnull)block
    NS_SWIFT_NAME(addOnSession(block:));

/**
 * Remove a callback that would be invoked before a session is sent to Bugsnag.
 *
 * @param block The block to be removed.
 */
- (void)removeOnSessionBlock:(BugsnagOnSessionBlock _Nonnull )block
    NS_SWIFT_NAME(removeOnSession(block:));

// =============================================================================
// MARK: - Other methods
// =============================================================================

/**
 * Retrieves the context - a general summary of what was happening in the application
 */
 @property (copy, nullable, atomic) NSString *context;

/**
 * @return YES if Bugsnag has been started and the previous launch crashed
 */
- (BOOL)appDidCrashLastLaunch __attribute__((deprecated("use 'lastRunInfo.crashed' instead")));

/**
 * Information about the last run of the app, and whether it crashed.
 */
@property (readonly, nullable, nonatomic) BugsnagLastRunInfo *lastRunInfo;

/**
 * Tells Bugsnag that your app has finished launching.
 *
 * Errors reported after calling this method will have the `BugsnagAppWithState.isLaunching`
 * property set to false.
 */
- (void)markLaunchCompleted;

// =============================================================================
// MARK: - User
// =============================================================================

/**
 * The current user
 */
- (BugsnagUser *_Nonnull)user;

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

@end
