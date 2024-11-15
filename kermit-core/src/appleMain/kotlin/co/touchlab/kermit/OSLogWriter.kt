/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

import co.touchlab.kermit.darwin.*
import kotlinx.cinterop.ExperimentalForeignApi
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.os_log_type_t
import kotlin.experimental.ExperimentalNativeApi

/**
 * Write log statements to darwin oslog.
 */
open class OSLogWriter internal constructor(
    private val messageStringFormatter: MessageStringFormatter,
    private val darwinLogger: DarwinLogger,
) : LogWriter() {

    constructor(messageStringFormatter: MessageStringFormatter = DefaultFormatter, subsystem: String = "", category: String = "", publicLogging: Boolean = false) : this(
        messageStringFormatter,
        DarwinLoggerActual(subsystem, category, publicLogging),
    )

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        callLog(
            severity, formatMessage(
                severity = severity,
                message = Message(message),
                tag = Tag(tag)
            ), throwable
        )
    }

    // Added to do some testing on log format. https://github.com/touchlab/Kermit/issues/243
    open fun callLog(severity: Severity, message: String, throwable: Throwable?) {
        val osLogSeverity = kermitSeverityToOsLogType(severity)
        darwinLogger.log(osLogSeverity, message)
        if (throwable != null) {
            logThrowable(osLogSeverity, throwable)
        }
    }

    @OptIn(ExperimentalNativeApi::class)
    open fun logThrowable(osLogSeverity: os_log_type_t, throwable: Throwable) {
        darwinLogger.log(osLogSeverity, throwable.getStackTrace().joinToString("\n"))
    }

    private fun kermitSeverityToOsLogType(severity: Severity): os_log_type_t = when (severity) {
        Severity.Verbose, Severity.Debug -> OS_LOG_TYPE_DEBUG
        Severity.Info -> OS_LOG_TYPE_INFO
        Severity.Warn -> OS_LOG_TYPE_DEFAULT
        Severity.Error -> OS_LOG_TYPE_ERROR
        Severity.Assert -> OS_LOG_TYPE_FAULT
    }

    open fun formatMessage(severity: Severity, tag: Tag, message: Message): String =
        messageStringFormatter.formatMessage(null, tag, message)
}


internal interface DarwinLogger {
    fun log(osLogSeverity: os_log_type_t, message: String)
}

@OptIn(ExperimentalForeignApi::class)
private class DarwinLoggerActual(subsystem: String, category: String, publicLogging: Boolean) : DarwinLogger {
    private val logger = darwin_log_create(subsystem, category)!!
    // see https://developer.apple.com/documentation/os/logging/generating_log_messages_from_your_code?language=objc
    // iOS considers everything coming from Kermit as a dynamic string, so without publicLogging=true, all logs are
    // private
    private val darwinLogFn: (osLogSeverity: os_log_type_t, message: String) -> Unit = if (publicLogging) {
        { osLogSeverity, message -> darwin_log_public_with_type(logger, osLogSeverity, message) }
    } else {
        { osLogSeverity, message -> darwin_log_with_type(logger, osLogSeverity, message) }
    }

    override fun log(osLogSeverity: os_log_type_t, message: String) {
        darwinLogFn(osLogSeverity, message)
    }
}
