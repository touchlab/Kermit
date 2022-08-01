/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.bugsnag

import Bugsnag.Bugsnag as NSBugsnag
import Bugsnag.BSGErrorType
import Bugsnag.BugsnagError
import Bugsnag.BugsnagStackframe
import Bugsnag.stackframesWithCallStackReturnAddresses
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import com.rickclephas.kmp.nsexceptionkt.core.asNSException
import com.rickclephas.kmp.nsexceptionkt.core.causes
import platform.Foundation.NSException

@ExperimentalKermitApi
actual class BugsnagLogWriter actual constructor(
    private val minSeverity: Severity,
    private val minCrashSeverity: Severity,
    private val printTag: Boolean
) : LogWriter() {
    init {
        assert(minSeverity <= minCrashSeverity) {
            "minSeverity ($minSeverity) cannot be greater than minCrashSeverity ($minCrashSeverity)"
        }
    }

    override fun isLoggable(severity: Severity): Boolean = severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        Bugsnag.leaveBreadcrumbWithMessage(
            if (printTag) {
                "$tag : $message"
            } else {
                message
            }
        )
        if (throwable != null && severity >= minCrashSeverity) {
            sendException(throwable)
        }
    }

    private fun sendException(throwable: Throwable) {
        notifyThrowable(throwable)
    }
}

internal fun notifyThrowable(throwable: Throwable) {
    val exception = throwable.asNSException()
    val causes = throwable.causes.map { it.asNSException() }
    // Notify will persist unhandled events, so we can safely terminate afterwards.
    // https://github.com/bugsnag/bugsnag-cocoa/blob/6bcd46f5f8dc06ac26537875d501f02b27d219a9/Bugsnag/Client/BugsnagClient.m#L744
    NSBugsnag.notify(exception) { event ->
        if (event == null) return@notify true
        if (causes.isNotEmpty()) {
            event.errors += causes.map { it.asBugsnagError() }
        }
        true
    }
}

internal fun NSException.asBugsnagError(): BugsnagError = BugsnagError().apply {
    errorClass = name
    errorMessage = reason
    stacktrace = BugsnagStackframe.stackframesWithCallStackReturnAddresses(callStackReturnAddresses)
    type = BSGErrorType.BSGErrorTypeCocoa
}