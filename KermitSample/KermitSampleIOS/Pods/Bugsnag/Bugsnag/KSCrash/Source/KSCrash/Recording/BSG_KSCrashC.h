//
//  BSG_KSCrashC.h
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

/* Primary C entry point into the crash reporting system.
 */

#ifndef HDR_BSG_KSCrashC_h
#define HDR_BSG_KSCrashC_h

#ifdef __cplusplus
extern "C" {
#endif

#include "BSG_KSCrashContext.h"

#include <stdbool.h>

/** Install the crash reporter. The reporter will record the next crash and then
 * terminate the program.
 *
 * @param crashReportFilePath The file to store the next crash report to.
 *
 * @param recrashReportFilePath If the system crashes during crash handling,
 *                              store a second, minimal report here.
 *
 * @param stateFilePath File to store persistent state in.
 *
 * @param crashID The unique identifier to assign to the next crash report.
 *
 * @return The crash types that are being handled.
 */
BSG_KSCrashType bsg_kscrash_install(const char *const crashReportFilePath,
                                    const char *const recrashReportFilePath,
                                    const char *stateFilePath,
                                    const char *crashID);

/** Set the crash types that will be handled.
 * Some crash types may not be enabled depending on circumstances (e.g. running
 * in a debugger).
 *
 * @param crashTypes The crash types to handle.
 *
 * @return The crash types that are now behing handled. If BSG_KSCrash has been
 *         installed, the return value represents the crash sentries that were
 *         successfully installed. Otherwise it represents which sentries it
 *         will attempt to activate when BSG_KSCrash installs.
 */
BSG_KSCrashType bsg_kscrash_setHandlingCrashTypes(BSG_KSCrashType crashTypes);

/** Reinstall the crash reporter. Useful for resetting the crash reporter
 * after a "soft" crash.
 *
 * @param crashReportFilePath The file to store the next crash report to.
 *
 * @param recrashReportFilePath If the system crashes during crash handling,
 *                              store a second, minimal report here.
 *
 * @param stateFilePath File to store persistent state in.
 *
 * @param crashID The unique identifier to assign to the next crash report.
 */
void bsg_kscrash_reinstall(const char *const crashReportFilePath,
                           const char *const recrashReportFilePath,
                           const char *const stateFilePath,
                           const char *const crashID);

/** Set the user-supplied data in JSON format.
 *
 * @param userInfoJSON Pre-baked JSON containing user-supplied information.
 *                     NULL = delete.
 */
void bsg_kscrash_setUserInfoJSON(const char *const userInfoJSON);

/** Set whether or not to print a stack trace to stdout when a crash occurs.
 *
 * Default: false
 */
void bsg_kscrash_setPrintTraceToStdout(bool printTraceToStdout);

/** If true, introspect memory contents during a crash.
 * Any Objective-C objects or C strings near the stack pointer or referenced by
 * cpu registers or exceptions will be recorded in the crash report, along with
 * their contents.
 *
 * Default: false
 */
void bsg_kscrash_setIntrospectMemory(bool introspectMemory);

/** Set the callback to invoke upon a crash.
 *
 * WARNING: Only call async-safe functions from this function! DO NOT call
 * Objective-C methods!!!
 *
 * @param onCrashNotify Function to call during a crash report to give the
 *                      callee an opportunity to add to the report.
 *                      NULL = ignore.
 *
 * Default: NULL
 */
void bsg_kscrash_setCrashNotifyCallback(
    const BSGReportCallback onCrashNotify);

/** If YES, user reported exceptions even if a debugger is attached
 *
 * Default: NO
 */
void bsg_kscrash_setReportWhenDebuggerIsAttached(
    bool reportWhenDebuggerIsAttached);

void bsg_kscrash_setThreadTracingEnabled(bool threadTracingEnabled);

/**
 * The current crash context
 */
BSG_KSCrash_Context *crashContext(void);

#ifdef __cplusplus
}
#endif

#endif // HDR_KSCrashC_h
