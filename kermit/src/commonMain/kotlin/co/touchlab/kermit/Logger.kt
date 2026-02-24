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
     * Log a message with [Severity.Verbose]. This is the lowest severity level.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("v(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun v(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Verbose, withTag, throwable, message)
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
     * Log a message with [Severity.Debug]. This is above 'Verbose'.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("d(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun d(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Debug, withTag, throwable, message)
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
     * Log a message with [Severity.Info]. This is above 'Debug'.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("i(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun i(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Info, withTag, throwable, message)
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
     * Log a message with [Severity.Warn]. This is above 'Info'.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("w(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun w(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Warn, withTag, throwable, message)
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
     * Log a message with [Severity.Error]. This is above 'Warn'.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("e(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun e(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Error, withTag, throwable, message)
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
     * Log a message with [Severity.Assert]. This is the highest severity level.
     *
     * This overload places [withTag] first, mirroring the former global-logger companion API
     * and allowing positional tag-first call sites to compile without changes.
     *
     * @param withTag Tag to associate with the log message.
     * @param throwable Optional throwable to log.
     * @param message Lambda returning the message to log.
     */
    @Deprecated(
        message = "Prefer the throwable-first overload and pass the tag using the named `tag` parameter.",
        replaceWith = ReplaceWith("a(throwable = throwable, tag = withTag, message = message)"),
    )
    @JvmOverloads
    inline fun a(withTag: String, throwable: Throwable? = null, message: () -> String) {
        logBlock(Severity.Assert, withTag, throwable, message)
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
    }
}

internal expect var defaultTag: String
