//
//  Bugsnag.h
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

/**
 * Types of breadcrumbs
 */
typedef NS_ENUM(NSUInteger, BSGBreadcrumbType) {
    /**
     *  Any breadcrumb sent via Bugsnag.leaveBreadcrumb()
     */
    BSGBreadcrumbTypeManual,
    /**
     *  A call to Bugsnag.notify() (internal use only)
     */
    BSGBreadcrumbTypeError,
    /**
     *  A log message
     */
    BSGBreadcrumbTypeLog,
    /**
     *  A navigation action, such as pushing a view controller or dismissing an alert
     */
    BSGBreadcrumbTypeNavigation,
    /**
     *  A background process, such performing a database query
     */
    BSGBreadcrumbTypeProcess,
    /**
     *  A network request
     */
    BSGBreadcrumbTypeRequest,
    /**
     *  Change in application or view state
     */
    BSGBreadcrumbTypeState,
    /**
     *  A user event, such as authentication or control events
     */
    BSGBreadcrumbTypeUser,
};

/**
 * Static access to a Bugsnag Client, the easiest way to use Bugsnag in your app.
 */
@interface Bugsnag : NSObject// <BugsnagClassLevelMetadataStore>

/**
 * All Bugsnag access is class-level.  Prevent the creation of instances.
 */
- (instancetype _Nonnull )init NS_UNAVAILABLE NS_SWIFT_UNAVAILABLE("Use class methods to initialise Bugsnag.");

// =============================================================================
// MARK: - Notify
// =============================================================================

/**
 * Send a custom or caught exception to Bugsnag.
 *
 * The exception will be sent to Bugsnag in the background allowing your
 * app to continue running.
 *
 * @param exception  The exception.
 */
+ (void)notify:(NSException *_Nonnull)exception;

// =============================================================================
// MARK: - Breadcrumbs
// =============================================================================

/**
 * Leave a "breadcrumb" log message, representing an action that occurred
 * in your app, to aid with debugging.
 *
 * @param message  the log message to leave
 */
+ (void)leaveBreadcrumbWithMessage:(NSString *_Nonnull)message;

/**
 * Leave a "breadcrumb" log message, representing an action that occurred
 * in your app, to aid with debugging, along with additional metadata and
 * a type.
 *
 * @param message The log message to leave.
 * @param metadata Diagnostic data relating to the breadcrumb.
 *                 Values should be serializable to JSON with NSJSONSerialization.
 * @param type A BSGBreadcrumbTypeValue denoting the type of breadcrumb.
 */
+ (void)leaveBreadcrumbWithMessage:(NSString *_Nonnull)message
                          metadata:(NSDictionary *_Nullable)metadata
                           andType:(BSGBreadcrumbType)type
    NS_SWIFT_NAME(leaveBreadcrumb(_:metadata:type:));
@end
