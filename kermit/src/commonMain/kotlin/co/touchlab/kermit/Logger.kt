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
    /**
     * Returns a new [Logger] instance with the specified [tag].
     *
     * @param tag The tag to associate with the new logger instance.
     * @return A new [Logger] with the given tag.
     */
    fun withTag(tag: String): Logger = Logger(this.config, tag)

    /**
     * Log a message with [Severity.Verbose]. This is the lowest severity level.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun v(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Verbose, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Debug]. This is above 'Verbose'.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun d(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Debug, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Info]. This is above 'Debug'.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun i(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Info, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Warn]. This is above 'Info'.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun w(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Warn, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Error]. This is above 'Warn'.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun e(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Error, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Assert]. This is the highest severity level.
     *
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     * @param message Lambda returning the message to log.
     */
    @JvmOverloads
    inline fun a(throwable: Throwable? = null, tag: String = this.tag, message: () -> String) {
        logBlock(Severity.Assert, tag, throwable, message)
    }

    /**
     * Log a message with [Severity.Verbose] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun v(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Verbose, tag, throwable, messageString)
    }

    /**
     * Log a message with [Severity.Debug] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun d(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Debug, tag, throwable, messageString)
    }

    /**
     * Log a message with [Severity.Info] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun i(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Info, tag, throwable, messageString)
    }

    /**
     * Log a message with [Severity.Warn] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun w(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Warn, tag, throwable, messageString)
    }

    /**
     * Log a message with [Severity.Error] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun e(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Error, tag, throwable, messageString)
    }

    /**
     * Log a message with [Severity.Assert] using a string message.
     *
     * @param messageString The message to log.
     * @param throwable Optional throwable to log.
     * @param tag Tag to associate with the log message. Defaults to this logger's tag.
     */
    @JvmOverloads
    inline fun a(messageString: String, throwable: Throwable? = null, tag: String = this.tag) {
        log(Severity.Assert, tag, throwable, messageString)
    }

    @Suppress("unused")
    companion object : Logger(mutableLoggerConfigInit(listOf(platformLogWriter())), "") {
        /**
         * The default tag used for global logging when not otherwise specified.
         */
        override val tag: String
            get() = defaultTag

        /**
         * Set the minimum [Severity] for log messages to be processed by the global logger.
         *
         * @param severity The minimum severity to log.
         */
        fun setMinSeverity(severity: Severity) {
            mutableConfig.minSeverity = severity
        }

        /**
         * Replace the list of [LogWriter]s for the global logger.
         *
         * @param logWriters The new list of log writers.
         */
        fun setLogWriters(logWriters: List<LogWriter>) {
            mutableConfig.logWriterList = logWriters
        }

        /**
         * Replace the list of [LogWriter]s for the global logger.
         *
         * @param logWriter The new log writers.
         */
        fun setLogWriters(vararg logWriter: LogWriter) {
            mutableConfig.logWriterList = logWriter.toList()
        }

        /**
         * Add one or more [LogWriter]s to the global logger, prepending them to the current list.
         *
         * @param logWriter The log writers to add.
         */
        fun addLogWriter(vararg logWriter: LogWriter) {
            mutableConfig.logWriterList = logWriter.toList() + mutableConfig.logWriterList
        }

        /**
         * Set the default tag for the global logger.
         *
         * @param tag The tag to use for global log messages.
         */
        fun setTag(tag: String) {
            defaultTag = tag
        }

        /**
         * Log a message with [Severity.Verbose] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun v(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Verbose) {
                log(Severity.Verbose, tag, throwable, message())
            }
        }

        /**
         * Log a message with [Severity.Debug] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun d(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Debug) {
                log(Severity.Debug, tag, throwable, message())
            }
        }

        /**
         * Log a message with [Severity.Info] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun i(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Info) {
                log(Severity.Info, tag, throwable, message())
            }
        }

        /**
         * Log a message with [Severity.Warn] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun w(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Warn) {
                log(Severity.Warn, tag, throwable, message())
            }
        }

        /**
         * Log a message with [Severity.Error] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun e(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Error) {
                log(Severity.Error, tag, throwable, message())
            }
        }

        /**
         * Log a message with [Severity.Assert] using the global logger.
         *
         * @param tag Tag to associate with the log message.
         * @param throwable Optional throwable to log.
         * @param message Lambda returning the message to log.
         */
        fun a(tag: String, throwable: Throwable? = null, message: () -> String) {
            if (config.minSeverity <= Severity.Assert) {
                log(Severity.Assert, tag, throwable, message())
            }
        }
    }
}

internal expect var defaultTag: String
