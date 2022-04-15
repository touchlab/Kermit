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

import co.touchlab.crashkios.transformException
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.cinterop.convert
import platform.Foundation.NSException
import platform.Foundation.NSNumber
import platform.darwin.NSInteger

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
        transformException(throwable) { name, description, addresses ->
            Bugsnag.notify(BugsnagNSException(addresses, name, description))
        }
    }
}

class BugsnagNSException(stackTrace: List<Long>, exceptionType: String, message: String) : NSException(name = exceptionType, reason = message, userInfo = null) {
    private val _callStackReturnAddresses:List<NSNumber>
    init {
        _callStackReturnAddresses = stackTrace.map {
            @Suppress("CAST_NEVER_SUCCEEDS")
            it.convert<NSInteger>() as NSNumber
        }
    }

    override fun callStackReturnAddresses(): List<*> {
        return _callStackReturnAddresses
    }
}
