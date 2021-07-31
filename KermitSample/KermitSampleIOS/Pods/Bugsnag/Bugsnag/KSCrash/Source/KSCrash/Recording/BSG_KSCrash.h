//
//  BSG_KSCrash.h
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

#import <Foundation/Foundation.h>

#import "BSG_KSCrashReportWriter.h"
#import "BSG_KSCrashType.h"

/**
 * Reports any crashes that occur in the application.
 *
 * The crash reports will be located in $APP_HOME/Library/Caches/KSCrashReports
 */
@interface BSG_KSCrash : NSObject

/** A dictionary containing any info you'd like to appear in crash reports. Must
 * contain only JSON-safe data: NSString for keys, and NSDictionary, NSArray,
 * NSString, NSDate, and NSNumber for values.
 *
 * Default: nil
 */
@property(nonatomic, readwrite, retain) NSDictionary *userInfo;

/** If YES, introspect memory contents during a crash.
 * Any Objective-C objects or C strings near the stack pointer or referenced by
 * cpu registers or exceptions will be recorded in the crash report, along with
 * their contents.
 *
 * Default: YES
 */
@property(nonatomic, readwrite, assign) bool introspectMemory;

/** Get the singleton instance of the crash reporter.
 */
+ (BSG_KSCrash *)sharedInstance;

/**
 * Install the crash reporter.
 *
 * @return The crash types that are now behing handled, representing the crash
 *         sentries that were successfully installed.
 */
- (BSG_KSCrashType)install:(BSG_KSCrashType)crashTypes directory:(NSString *)directory;

/**
 * Collects information about the application's foreground state (duration in foreground/background)
 */
- (NSDictionary *)captureAppStats;

/** If YES, reports will be sent even if a debugger is attached
 *
 * Default: NO
 */
@property(nonatomic, readwrite, assign) BOOL reportWhenDebuggerIsAttached;

/**
* The methodology used for tracing threads.
 */
@property(nonatomic, readwrite, assign) BOOL threadTracingEnabled;

@end

//! Project version number for BSG_KSCrashFramework.
FOUNDATION_EXPORT const double BSG_KSCrashFrameworkVersionNumber;

//! Project version string for BSG_KSCrashFramework.
FOUNDATION_EXPORT const unsigned char BSG_KSCrashFrameworkVersionString[];
