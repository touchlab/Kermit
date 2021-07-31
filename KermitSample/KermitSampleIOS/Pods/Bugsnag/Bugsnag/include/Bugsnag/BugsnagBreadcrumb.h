//
//  BugsnagBreadcrumb.h
//
//  Created by Delisa Mason on 9/16/15.
//
//  Copyright (c) 2015 Bugsnag, Inc. All rights reserved.
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

#ifndef NS_DESIGNATED_INITIALIZER
#if __has_attribute(objc_designated_initializer)
#define NS_DESIGNATED_INITIALIZER __attribute__((objc_designated_initializer))
#else
#define NS_DESIGNATED_INITIALIZER
#endif
#endif

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
 * Types of breadcrumbs which can be reported
 */
typedef NS_OPTIONS(NSUInteger, BSGEnabledBreadcrumbType) {
    BSGEnabledBreadcrumbTypeNone       = 0,
    BSGEnabledBreadcrumbTypeState      = 1 << 1,
    BSGEnabledBreadcrumbTypeUser       = 1 << 2,
    BSGEnabledBreadcrumbTypeLog        = 1 << 3,
    BSGEnabledBreadcrumbTypeNavigation = 1 << 4,
    BSGEnabledBreadcrumbTypeRequest    = 1 << 5,
    BSGEnabledBreadcrumbTypeProcess    = 1 << 6,
    BSGEnabledBreadcrumbTypeError      = 1 << 7,
    BSGEnabledBreadcrumbTypeAll = BSGEnabledBreadcrumbTypeState
                                | BSGEnabledBreadcrumbTypeUser
                                | BSGEnabledBreadcrumbTypeLog
                                | BSGEnabledBreadcrumbTypeNavigation
                                | BSGEnabledBreadcrumbTypeRequest
                                | BSGEnabledBreadcrumbTypeProcess
                                | BSGEnabledBreadcrumbTypeError,
};

/**
 * A short log message, representing an action that occurred in your app, to aid with debugging.
 */
@class BugsnagBreadcrumb;

@interface BugsnagBreadcrumb : NSObject

/**
 * The date when the breadcrumb was left
 */
@property (readonly, nullable, nonatomic) NSDate *timestamp;

/**
 * The type of breadcrumb
 */
@property (readwrite, nonatomic) BSGBreadcrumbType type;

/**
 * The description of the breadcrumb
 */
@property (readwrite, copy, nonnull, nonatomic) NSString *message;

/**
 * Diagnostic data relating to the breadcrumb.
 * 
 * The dictionary should be a valid JSON object.
 */
@property (readwrite, copy, nonnull, nonatomic) NSDictionary *metadata;

@end
