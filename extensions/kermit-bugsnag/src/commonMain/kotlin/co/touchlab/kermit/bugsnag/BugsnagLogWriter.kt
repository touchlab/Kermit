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

import co.touchlab.crashkios.bugsnag.BugsnagCalls
import co.touchlab.crashkios.bugsnag.BugsnagCallsActual
import co.touchlab.crashkios.bugsnag.enableBugsnag
import co.touchlab.kermit.DefaultFormatter
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Message
import co.touchlab.kermit.MessageStringFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.Tag

@ExperimentalKermitApi
class BugsnagLogWriter(
    private val minSeverity: Severity = Severity.Info,
    private val minCrashSeverity: Severity? = Severity.Warn,
    private val messageStringFormatter: MessageStringFormatter = DefaultFormatter,
) : LogWriter() {

    private val bugsnagCalls: BugsnagCalls = BugsnagCallsActual()

    init {
        if (minCrashSeverity != null && minSeverity > minCrashSeverity) {
            throw IllegalArgumentException("minSeverity ($minSeverity) cannot be greater than minCrashSeverity ($minCrashSeverity)")
        }

        enableBugsnag()
    }

    override fun isLoggable(tag: String, severity: Severity): Boolean = severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        bugsnagCalls.logMessage(
            messageStringFormatter.formatMessage(severity, Tag(tag), Message(message)),
        )
        if (throwable != null && minCrashSeverity != null && severity >= minCrashSeverity) {
            bugsnagCalls.sendHandledException(throwable)
        }
    }
}
