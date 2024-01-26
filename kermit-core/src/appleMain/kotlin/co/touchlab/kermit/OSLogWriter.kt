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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ptr
import platform.darwin.OS_LOG_DEFAULT
import platform.darwin.OS_LOG_TYPE_DEBUG
import platform.darwin.OS_LOG_TYPE_DEFAULT
import platform.darwin.OS_LOG_TYPE_ERROR
import platform.darwin.OS_LOG_TYPE_FAULT
import platform.darwin.OS_LOG_TYPE_INFO
import platform.darwin.__dso_handle
import platform.darwin._os_log_internal
import kotlin.experimental.ExperimentalNativeApi

/**
 * Write log statements to darwin oslog.
 */
open class OSLogWriter internal constructor(
    private val messageStringFormatter: MessageStringFormatter,
    private val darwinLogger: DarwinLogger
) : LogWriter() {

    constructor(messageStringFormatter: MessageStringFormatter = DefaultFormatter) : this(
        messageStringFormatter,
        DarwinLoggerActual
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
    open fun logThrowable(osLogSeverity: UByte, throwable: Throwable) {
        darwinLogger.log(osLogSeverity, throwable.getStackTrace().joinToString("\n"))
    }

    private fun kermitSeverityToOsLogType(severity: Severity): UByte = when (severity) {
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
    fun log(osLogSeverity: UByte, message: String)
}

private object DarwinLoggerActual : DarwinLogger {
    @OptIn(ExperimentalForeignApi::class)
    override fun log(osLogSeverity: UByte, message: String) {
        _os_log_internal(
            __dso_handle.ptr,
            OS_LOG_DEFAULT,
            osLogSeverity,
            "%s",
            message
        )
    }
}