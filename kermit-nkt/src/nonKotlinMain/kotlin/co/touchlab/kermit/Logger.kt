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

import kotlin.native.concurrent.ThreadLocal

class LoggerMethods internal constructor(private var logger: Logger = Logger, private var severity: Severity = Severity.Verbose) {
    internal fun applyValues(logger: Logger, severity: Severity): LoggerMethods {
        this.logger = logger
        this.severity = severity
        return this
    }

    fun log(tag: String, throwable: Throwable, message: () -> String) {
        logger.logBlock(severity, tag, throwable, message)
    }

    fun log(tag: String, message: () -> String) {
        logger.logBlock(severity, tag, null, message)
    }

    fun log(throwable: Throwable, message: () -> String) {
        logger.logBlock(severity, logger.tag, throwable, message)
    }

    fun log(message: () -> String) {
        logger.logBlock(severity, logger.tag, null, message)
    }

    fun log(tag: String, throwable: Throwable, messageString: String) {
        logger.log(severity, tag, throwable, messageString)
    }

    fun log(tag: String, messageString: String) {
        logger.log(severity, tag, null, messageString)
    }

    fun log(throwable: Throwable, messageString: String) {
        logger.log(severity, logger.tag, throwable, messageString)
    }

    fun log(messageString: String) {
        logger.log(severity, logger.tag, null, messageString)
    }
}

@ThreadLocal
private val loggerMethods = LoggerMethods()

val Logger.v: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Verbose)

val Logger.d: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Debug)

val Logger.i: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Info)

val Logger.w: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Warn)

val Logger.e: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Error)

val Logger.a: LoggerMethods
    get() = loggerMethods.applyValues(this, Severity.Assert)

// These can be accessed from Swift with LoggerKt.d.log(message: "some log")
val v: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Verbose)

val d: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Debug)

val i: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Info)

val w: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Warn)

val e: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Error)

val a: LoggerMethods
    get() = loggerMethods.applyValues(Logger, Severity.Assert)