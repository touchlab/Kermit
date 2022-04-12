/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package co.touchlab.kermit

@Suppress("unused")
open class Logger(
    val config: LoggerConfig,
    open val tag: String = "Kermit"
) {
    fun withTag(tag: String): Logger {
        return Logger(this.config, tag)
    }

    /*
     * We could reduce the methods on this object, but the native objc
     * export can't use default arguments, so we have a few extra methods defined.
     */
    inline fun v(message: () -> String) {
        if (config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, null, message())
    }

    inline fun v(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, throwable, message())
    }

    fun v(message: String) {
        if (config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, null, message)
    }
    fun v(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, throwable, message)
    }

    inline fun d(message: () -> String) {
        if (config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, null, message())
    }

    inline fun d(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, throwable, message())
    }

    fun d(message: String) {
        if (config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, null, message)
    }
    fun d(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, throwable, message)
    }

    inline fun i(message: () -> String) {
        if (config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, null, message())
    }

    inline fun i(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, throwable, message())
    }

    fun i(message: String) {
        if (config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, null, message)
    }
    fun i(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, throwable, message)
    }

    inline fun w(message: () -> String) {
        if (config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, null, message())
    }

    inline fun w(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, throwable, message())
    }

    fun w(message: String) {
        if (config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, null, message)
    }
    fun w(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, throwable, message)
    }

    inline fun e(message: () -> String) {
        if (config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, null, message())
    }

    inline fun e(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, throwable, message())
    }

    fun e(message: String) {
        if (config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, null, message)
    }
    fun e(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, throwable, message)
    }

    inline fun a(message: () -> String) {
        if (config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, null, message())
    }

    inline fun a(throwable: Throwable, message: () -> String) {
        if (config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, throwable, message())
    }

    fun a(message: String) {
        if (config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, null, message)
    }
    fun a(message: String, throwable: Throwable) {
        if (config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, throwable, message)
    }

    fun log(
        severity: Severity,
        tag: String = this.tag,
        throwable: Throwable?,
        message: String
    ) {
        processLog(
            severity,
            tag,
            throwable,
            message
        ) { processedMessage, processedTag, processedThrowable ->
            log(severity, processedMessage, processedTag, processedThrowable)
        }
    }

    private inline fun processLog(
        severity: Severity,
        tag: String,
        throwable: Throwable?,
        message: String,
        loggingCall: LogWriter.(message: String, tag: String, throwable: Throwable?) -> Unit
    ) {
        config.logWriterList.forEach {
            if (it.isLoggable(severity)) {
                it.loggingCall(message, tag, throwable)
            }
        }
    }

    @Suppress("unused")
    companion object : Logger(LoggerGlobal.defaultConfig) {
        override val tag:String
            get() = defaultTag

        fun setMinSeverity(severity: Severity) {
            LoggerGlobal.defaultConfig.minSeverity = severity
        }

        fun setLogWriters(logWriters: List<LogWriter>) {
            LoggerGlobal.defaultConfig.logWriterList = logWriters
        }

        fun setLogWriters(vararg logWriter: LogWriter) {
            LoggerGlobal.defaultConfig.logWriterList = logWriter.toList()
        }

        fun addLogWriter(vararg logWriter: LogWriter) {
            LoggerGlobal.defaultConfig.logWriterList = logWriter.toList() + LoggerGlobal.defaultConfig.logWriterList
        }

        fun setTag(tag: String) {
            defaultTag = tag
        }

        fun v(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Verbose)
                log(Severity.Verbose, tag, throwable, message())
        }

        fun d(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Debug)
                log(Severity.Debug, tag, throwable, message())
        }

        fun i(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Info)
                log(Severity.Info, tag, throwable, message())
        }

        fun w(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Warn)
                log(Severity.Warn, tag, throwable, message())
        }

        fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Error)
                log(Severity.Error, tag, throwable, message())
        }

        fun a(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Assert)
                log(Severity.Assert, tag, throwable, message())
        }
    }
}

internal object LoggerGlobal {
    val defaultConfig = mutableKermitConfigInit(listOf(platformLogWriter()))
}

@kotlin.native.concurrent.SharedImmutable
internal val DEFAULT_MIN_SEVERITY = Severity.Verbose
internal const val DEFAULT_TAG = "Kermit"

internal expect fun mutableKermitConfigInit(logWriters: List<LogWriter>): MutableLoggerConfig
internal expect var defaultTag: String