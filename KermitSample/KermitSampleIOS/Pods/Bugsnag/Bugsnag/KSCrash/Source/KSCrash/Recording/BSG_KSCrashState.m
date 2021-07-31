//
//  BSG_KSCrashState.c
//
//  Created by Karl Stenerud on 2012-02-05.
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

#include "BSG_KSCrashState.h"

#include "BSG_KSFileUtils.h"
#include "BSG_KSJSONCodec.h"
#include "BSG_KSJSONCodecObjC.h"
#include "BSG_KSMach.h"
#include "BSG_KSSystemInfo.h"

//#define BSG_KSLogger_LocalLevel TRACE
#include "BSG_KSLogger.h"

#if (TARGET_OS_TV || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
#import "BSGUIKit.h"
#endif
#include <errno.h>
#include <fcntl.h>
#include <mach/mach_time.h>
#include <stdlib.h>
#include <unistd.h>

// ============================================================================
#pragma mark - Constants -
// ============================================================================

#define BSG_kFormatVersion 1

#define BSG_kKeyFormatVersion "version"
#define BSG_kKeyCrashedLastLaunch "crashedLastLaunch"
#define BSG_kKeyActiveDurationSinceLastCrash "foregroundDurationSinceLastCrash"
#define BSG_kKeyBackgroundDurationSinceLastCrash                               \
    "backgroundDurationSinceLastCrash"
#define BSG_kKeyLaunchesSinceLastCrash "launchesSinceLastCrash"
#define BSG_kKeySessionsSinceLastCrash "sessionsSinceLastCrash"

// ============================================================================
#pragma mark - Globals -
// ============================================================================

/** Location where stat file is stored. */
static const char *bsg_g_stateFilePath;

/** Current state. */
static BSG_KSCrash_State *bsg_g_state;

// Avoiding static functions due to linker issues.

// ============================================================================
#pragma mark - JSON Encoding -
// ============================================================================

int bsg_kscrashstate_i_onBooleanElement(const char *const name,
                                        const bool value,
                                        void *const userData) {
    BSG_KSCrash_State *state = userData;

    if (strcmp(name, BSG_kKeyCrashedLastLaunch) == 0) {
        state->crashedLastLaunch = value;
    }

    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onFloatingPointElement(const char *const name,
                                              const double value,
                                              void *const userData) {
    BSG_KSCrash_State *state = userData;

    if (strcmp(name, BSG_kKeyActiveDurationSinceLastCrash) == 0) {
        state->foregroundDurationSinceLastCrash = value;
    }
    if (strcmp(name, BSG_kKeyBackgroundDurationSinceLastCrash) == 0) {
        state->backgroundDurationSinceLastCrash = value;
    }

    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onIntegerElement(const char *const name,
                                        const long long value,
                                        void *const userData) {
    BSG_KSCrash_State *state = userData;

    if (strcmp(name, BSG_kKeyFormatVersion) == 0) {
        if (value != BSG_kFormatVersion) {
            BSG_KSLOG_ERROR(@"Expected version 1 but got %lld", value);
            return BSG_KSJSON_ERROR_INVALID_DATA;
        }
    } else if (strcmp(name, BSG_kKeyLaunchesSinceLastCrash) == 0) {
        state->launchesSinceLastCrash = (int)value;
    } else if (strcmp(name, BSG_kKeySessionsSinceLastCrash) == 0) {
        state->sessionsSinceLastCrash = (int)value;
    }

    // FP value might have been written as a whole number.
    return bsg_kscrashstate_i_onFloatingPointElement(name, (double)value, userData);
}

int bsg_kscrashstate_i_onNullElement(__unused const char *const name,
                                     __unused void *const userData) {
    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onStringElement(__unused const char *const name,
                                       __unused const char *const value,
                                       __unused void *const userData) {
    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onBeginObject(__unused const char *const name,
                                     __unused void *const userData) {
    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onBeginArray(__unused const char *const name,
                                    __unused void *const userData) {
    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onEndContainer(__unused void *const userData) {
    return BSG_KSJSON_OK;
}

int bsg_kscrashstate_i_onEndData(__unused void *const userData) {
    return BSG_KSJSON_OK;
}

/** Callback for adding JSON data.
 */
int bsg_kscrashstate_i_addJSONData(const char *const data, const size_t length,
                                   void *const userData) {
    const int fd = *((int *)userData);
    const bool success = bsg_ksfuwriteBytesToFD(fd, data, (ssize_t)length);
    return success ? BSG_KSJSON_OK : BSG_KSJSON_ERROR_CANNOT_ADD_DATA;
}

// ============================================================================
#pragma mark - Utility -
// ============================================================================

/** Load the persistent state portion of a crash context.
 *
 * @param context The context to load into.
 *
 * @param path The path to the file to read.
 *
 * @return true if the operation was successful.
 */
bool bsg_kscrashstate_i_loadState(BSG_KSCrash_State *const context,
                                  const char *const path) {
    if (path == NULL) {
        return false;
    }
    NSString *file = [NSFileManager.defaultManager stringWithFileSystemRepresentation:path length:strlen(path)];
    if (!file) {
        BSG_KSLOG_ERROR(@"Invalid path: %s", path);
        return false;
    }
    NSError *error = nil;
    NSData *data = [NSData dataWithContentsOfFile:file options:0 error:&error];
    if (error != nil) {
        if (!(error.domain == NSCocoaErrorDomain && error.code == NSFileReadNoSuchFileError)) {
            BSG_KSLOG_ERROR(@"%s: Could not load file: %@", path, error);
        }
        return false;
    }
    id objectContext = [BSG_KSJSONCodec decode:data error:&error];
    if (error != nil) {
        BSG_KSLOG_ERROR(@"%s: Could not load file: %@", path, error);
        return false;
    }

    context->foregroundDurationSinceLastCrash = [objectContext[@"foregroundDurationSinceLastCrash"] doubleValue];
    context->foregroundDurationSinceLaunch = [objectContext[@"foregroundDurationSinceLaunch"] doubleValue];
    context->appLaunchTime = [objectContext[@"appLaunchTime"] unsignedLongLongValue];
    context->lastUpdateDurationsTime = [objectContext[@"appStateTransitionTime"] unsignedLongLongValue];
    context->launchesSinceLastCrash = [objectContext[@"launchesSinceLastCrash"] intValue];
    context->sessionsSinceLastCrash = [objectContext[@"sessionsSinceLastCrash"] intValue];
    context->sessionsSinceLaunch = [objectContext[@"sessionsSinceLaunch"] intValue];
    context->crashedLastLaunch = [objectContext[@"crashedLastLaunch"] boolValue];
    context->crashedThisLaunch = [objectContext[@"crashedThisLaunch"] boolValue];
    context->applicationIsInForeground = [objectContext[@"applicationIsInForeground"] boolValue];
    context->backgroundDurationSinceLaunch = [objectContext[@"backgroundDurationSinceLaunch"] doubleValue];
    context->backgroundDurationSinceLastCrash = [objectContext[@"backgroundDurationSinceLastCrash"] doubleValue];

    return true;
}

/** Save the persistent state portion of a crash context.
 *
 * @param state The context to save from.
 *
 * @param path The path to the file to create.
 *
 * @return true if the operation was successful.
 */
bool bsg_kscrashstate_i_saveState(const BSG_KSCrash_State *const state,
                                  const char *const path) {
    int fd = open(path, O_RDWR | O_CREAT | O_TRUNC, 0644);
    if (fd < 0) {
        BSG_KSLOG_ERROR(@"Could not open file %s for writing: %s", path,
                        strerror(errno));
        return false;
    }

    BSG_KSJSONEncodeContext JSONContext;
    bsg_ksjsonbeginEncode(&JSONContext, false, bsg_kscrashstate_i_addJSONData,
                          &fd);

    int result;
    if ((result = bsg_ksjsonbeginObject(&JSONContext, NULL)) != BSG_KSJSON_OK) {
        goto done;
    }
    if ((result = bsg_ksjsonaddIntegerElement(
             &JSONContext, BSG_kKeyFormatVersion, BSG_kFormatVersion)) !=
        BSG_KSJSON_OK) {
        goto done;
    }
    // Record this launch crashed state into "crashed last launch" field.
    if ((result = bsg_ksjsonaddBooleanElement(
             &JSONContext, BSG_kKeyCrashedLastLaunch,
             state->crashedThisLaunch)) != BSG_KSJSON_OK) {
        goto done;
    }
    if ((result = bsg_ksjsonaddFloatingPointElement(
             &JSONContext, BSG_kKeyActiveDurationSinceLastCrash,
             state->foregroundDurationSinceLastCrash)) != BSG_KSJSON_OK) {
        goto done;
    }
    if ((result = bsg_ksjsonaddFloatingPointElement(
             &JSONContext, BSG_kKeyBackgroundDurationSinceLastCrash,
             state->backgroundDurationSinceLastCrash)) != BSG_KSJSON_OK) {
        goto done;
    }
    if ((result = bsg_ksjsonaddIntegerElement(
             &JSONContext, BSG_kKeyLaunchesSinceLastCrash,
             state->launchesSinceLastCrash)) != BSG_KSJSON_OK) {
        goto done;
    }
    if ((result = bsg_ksjsonaddIntegerElement(
             &JSONContext, BSG_kKeySessionsSinceLastCrash,
             state->sessionsSinceLastCrash)) != BSG_KSJSON_OK) {
        goto done;
    }
    result = bsg_ksjsonendEncode(&JSONContext);

done:
    close(fd);

    if (result != BSG_KSJSON_OK) {
        BSG_KSLOG_ERROR(@"%s: %s", path, bsg_ksjsonstringForError(result));
        return false;
    }
    return true;
}

// ============================================================================
#pragma mark - API -
// ============================================================================

bool bsg_kscrashstate_init(const char *const stateFilePath,
                           BSG_KSCrash_State *const state) {
    bsg_g_stateFilePath = stateFilePath;
    bsg_g_state = state;

    bsg_kscrashstate_i_loadState(state, stateFilePath);

    state->sessionsSinceLaunch = 1;
    state->foregroundDurationSinceLaunch = 0;
    state->backgroundDurationSinceLaunch = 0;
    if (state->crashedLastLaunch) {
        state->foregroundDurationSinceLastCrash = 0;
        state->backgroundDurationSinceLastCrash = 0;
        state->launchesSinceLastCrash = 0;
        state->sessionsSinceLastCrash = 0;
    }
    state->crashedThisLaunch = false;

    // Simulate first transition to foreground
    state->launchesSinceLastCrash++;
    state->sessionsSinceLastCrash++;
#if (TARGET_OS_TV || TARGET_OS_IPHONE || TARGET_IPHONE_SIMULATOR)
    // On iOS/tvOS, the app may have launched in the background due to a fetch
    // event or notification
    UIApplicationState appState = [BSG_KSSystemInfo currentAppState];
    state->applicationIsInForeground = [BSG_KSSystemInfo isInForeground:appState];
#else
    state->applicationIsInForeground = true;
#endif

    return bsg_kscrashstate_i_saveState(state, stateFilePath);
}

void bsg_kscrashstate_notifyAppInForeground(const bool isInForeground) {
    BSG_KSCrash_State *const state = bsg_g_state;
    const char *const stateFilePath = bsg_g_stateFilePath;

    if (state->applicationIsInForeground == isInForeground) {
        return;
    }
    state->applicationIsInForeground = isInForeground;
    uint64_t timeNow = mach_absolute_time();
    double duration = bsg_ksmachtimeDifferenceInSeconds(
        timeNow, state->lastUpdateDurationsTime);
    if (isInForeground) {
        state->backgroundDurationSinceLaunch += duration;
        state->backgroundDurationSinceLastCrash += duration;
        state->sessionsSinceLastCrash++;
        state->sessionsSinceLaunch++;
    } else {
        state->foregroundDurationSinceLaunch += duration;
        state->foregroundDurationSinceLastCrash += duration;
        bsg_kscrashstate_i_saveState(state, stateFilePath);
    }
    state->lastUpdateDurationsTime = timeNow;
}

void bsg_kscrashstate_notifyAppTerminate(void) {
    BSG_KSCrash_State *const state = bsg_g_state;
    const char *const stateFilePath = bsg_g_stateFilePath;

    const double duration = bsg_ksmachtimeDifferenceInSeconds(
        mach_absolute_time(), state->lastUpdateDurationsTime);
    state->backgroundDurationSinceLastCrash += duration;
    bsg_kscrashstate_i_saveState(state, stateFilePath);
}

void bsg_kscrashstate_notifyAppCrash(void) {
    BSG_KSCrash_State *const state = bsg_g_state;
    const char *const stateFilePath = bsg_g_stateFilePath;
    bsg_kscrashstate_updateDurationStats(state);
    state->crashedThisLaunch = YES;
    bsg_kscrashstate_i_saveState(state, stateFilePath);
}

void bsg_kscrashstate_updateDurationStats(BSG_KSCrash_State *const state) {
    uint64_t timeNow = mach_absolute_time();
    const double duration = bsg_ksmachtimeDifferenceInSeconds(
        timeNow, state->lastUpdateDurationsTime ?: state->appLaunchTime);
    if (state->applicationIsInForeground) {
        state->foregroundDurationSinceLaunch += duration;
        state->foregroundDurationSinceLastCrash += duration;
    } else {
        state->backgroundDurationSinceLaunch += duration;
        state->backgroundDurationSinceLastCrash += duration;
    }
    state->lastUpdateDurationsTime = timeNow;
}

const BSG_KSCrash_State *bsg_kscrashstate_currentState(void) {
    return bsg_g_state;
}
