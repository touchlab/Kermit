//
//  BSG_KSCrashAdvanced.h
//
//  Created by Karl Stenerud on 2012-05-06.
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

#import "BSG_KSCrash.h"

/**
 * Advanced interface to the BSG_KSCrash system.
 */
@interface BSG_KSCrash (Advanced)

#pragma mark - Information -

/** Total active time elapsed since the last crash. */
@property(nonatomic, readonly, assign)
    NSTimeInterval foregroundDurationSinceLastCrash;

/** Total time backgrounded elapsed since the last crash. */
@property(nonatomic, readonly, assign)
    NSTimeInterval backgroundDurationSinceLastCrash;

/** Number of app launches since the last crash. */
@property(nonatomic, readonly, assign) int launchesSinceLastCrash;

/** Number of sessions (launch, resume from suspend) since last crash. */
@property(nonatomic, readonly, assign) int sessionsSinceLastCrash;

/** Total active time elapsed since launch. */
@property(nonatomic, readonly, assign) NSTimeInterval foregroundDurationSinceLaunch;

/** Total time backgrounded elapsed since launch. */
@property(nonatomic, readonly, assign)
    NSTimeInterval backgroundDurationSinceLaunch;

/** Number of sessions (launch, resume from suspend) since app launch. */
@property(nonatomic, readonly, assign) int sessionsSinceLaunch;

/** If true, the application crashed on the previous launch. */
@property(nonatomic, readonly, assign) BOOL crashedLastLaunch;

/** Max number of reports to store on disk before throwing older reports out.
 * (default 5) */
@property(nonatomic, readwrite, assign) int maxStoredReports;

#pragma mark - Configuration -

/** C Function to call during a crash report to give the callee an opportunity
 * to add to the report. NULL = ignore.
 *
 * WARNING: Only call async-safe functions from this function! DO NOT call
 * Objective-C methods!!!
 *
 * Note: If you use an installation, it will automatically set this property.
 *       Do not modify it in such a case.
 */
@property(nonatomic, readwrite, assign) BSGReportCallback onCrash;

/** Path where the log of BSG_KSCrash's activities will be written.
 * If nil, log entries will be printed to the console.
 *
 * This property cannot be set directly. Use one of the "redirectConsoleLogs"
 * methods instead.
 *
 * Default: nil
 */
@property(nonatomic, readonly, retain) NSString *logFilePath;

/** If YES, print a stack trace to stdout when a crash occurs.
 *
 * Default: NO
 */
@property(nonatomic, readwrite, assign) bool printTraceToStdout;

/** Redirect the log of BSG_KSCrash's activities from the console to the
 * specified log file.
 *
 * @param fullPath The path to the logfile (nil = log to console instead).
 * @param overwrite If true, overwrite the file (ignored if fullPath is nil).
 *
 * @return true if the operation was successful.
 */
- (BOOL)redirectConsoleLogsToFile:(NSString *)fullPath
                        overwrite:(BOOL)overwrite;

@end
