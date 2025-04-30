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
@file:Suppress("NOTHING_TO_INLINE")

package co.touchlab.kermit

import kotlin.jvm.JvmOverloads

/**
 * The default Logger API. This class can be a local instance, or use the global companion syntax.
 */
@Suppress("unused")
open class Logger(config: LoggerConfig, open val tag: String = "") : BaseLogger(config) {
    fun withTag(tag: String): Logger = Logger(this.config, tag)

    @JvmOverloads
    inline fun v(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Verbose, tag, throwable, message)
    }

    @JvmOverloads
    inline fun d(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Debug, tag, throwable, message)
    }

    @JvmOverloads
    inline fun i(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Info, tag, throwable, message)
    }

    @JvmOverloads
    inline fun w(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Warn, tag, throwable, message)
    }

    @JvmOverloads
    inline fun e(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Error, tag, throwable, message)
    }

    @JvmOverloads
    inline fun a(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Assert, tag, throwable, message)
    }

    @JvmOverloads
    inline fun v(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Verbose, tag, throwable, messageString)
    }

    @JvmOverloads
    inline fun d(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Debug, tag, throwable, messageString)
    }

    @JvmOverloads
    inline fun i(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Info, tag, throwable, messageString)
    }

    @JvmOverloads
    inline fun w(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Warn, tag, throwable, messageString)
    }

    @JvmOverloads
    inline fun e(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Error, tag, throwable, messageString)
    }

    @JvmOverloads
    inline fun a(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Assert, tag, throwable, messageString)
    }

    @Suppress("unused")
    companion object : Logger(mutableLoggerConfigInit(listOf(platformLogWriter())), "") {
        override val tag: String
            get() = defaultTag

        fun setMinSeverity(severity: Severity) {
            mutableConfig.minSeverity = severity
        }

        fun setLogWriters(logWriters: List<LogWriter>) {
            mutableConfig.logWriterList = logWriters
        }

        fun setLogWriters(vararg logWriter: LogWriter) {
            mutableConfig.logWriterList = logWriter.toList()
        }

        fun addLogWriter(vararg logWriter: LogWriter) {
            mutableConfig.logWriterList = logWriter.toList() + mutableConfig.logWriterList
        }

        fun setTag(tag: String) {
            defaultTag = tag
        }

        fun v(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Verbose) {
                log(Severity.Verbose, tag, throwable, message())
            }
        }

        fun d(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Debug) {
                log(Severity.Debug, tag, throwable, message())
            }
        }

        fun i(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Info) {
                log(Severity.Info, tag, throwable, message())
            }
        }

        fun w(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Warn) {
                log(Severity.Warn, tag, throwable, message())
            }
        }

        fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Error) {
                log(Severity.Error, tag, throwable, message())
            }
        }

        fun a(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Assert) {
                log(Severity.Assert, tag, throwable, message())
            }
        }
    }
}

internal expect var defaultTag: String
