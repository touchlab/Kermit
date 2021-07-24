/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

class Kermit(
    private val loggerList: List<Logger> = listOf(
        CommonLogger()
    ), val defaultTag: String = "Kermit"
) {
    val minSeverity:Severity = loggerList.minOfOrNull { when{
        it.isLoggable(Severity.Verbose) -> Severity.Verbose
        it.isLoggable(Severity.Debug) -> Severity.Debug
        it.isLoggable(Severity.Info) -> Severity.Info
        it.isLoggable(Severity.Warn) -> Severity.Warn
        it.isLoggable(Severity.Error) -> Severity.Error
        else -> Severity.Assert
    } } ?: Severity.Assert

    constructor(vararg loggers: Logger) : this(loggers.asList())
    constructor(logger: Logger) : this(listOf(logger))

    fun withTag(defaultTag: String): Kermit {
        return Kermit(this.loggerList, defaultTag)
    }

    inline fun v(throwable: Throwable? = null, message: () -> String){
        if(minSeverity <= Severity.Verbose)
            log(Severity.Verbose, defaultTag, throwable, message())
    }

    fun v(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Verbose)
            log(Severity.Verbose, defaultTag, throwable, message)
    }

    inline fun d(throwable: Throwable? = null, message: () -> String) {
        if(minSeverity <= Severity.Debug)
            log(Severity.Debug, defaultTag, throwable, message())
    }

    fun d(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Debug)
            log(Severity.Debug, defaultTag, throwable, message)
    }

    inline fun i(throwable: Throwable? = null, message: () -> String) {
        if(minSeverity <= Severity.Info)
            log(Severity.Info, defaultTag, throwable, message())
    }

    fun i(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Info)
            log(Severity.Info, defaultTag, throwable, message)
    }

    inline fun w(throwable: Throwable? = null, message: () -> String) {
        if(minSeverity <= Severity.Warn)
            log(Severity.Warn, defaultTag, throwable, message())
    }

    fun w(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Warn)
            log(Severity.Warn, defaultTag, throwable, message)
    }

    inline fun e(throwable: Throwable? = null, message: () -> String) {
        if(minSeverity <= Severity.Error)
            log(Severity.Error, defaultTag, throwable, message())
    }

    fun e(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Error)
            log(Severity.Error, defaultTag, throwable, message)
    }

    inline fun wtf(throwable: Throwable? = null, message: () -> String) {
        if(minSeverity <= Severity.Assert)
            log(Severity.Assert, defaultTag, throwable, message())
    }

    fun wtf(message: String, throwable: Throwable? = null){
        if(minSeverity <= Severity.Assert)
            log(Severity.Assert, defaultTag, throwable, message)
    }

    fun log(
        severity: Severity,
        tag: String = defaultTag,
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
        loggingCall: Logger.(message: String, tag: String, throwable: Throwable?) -> Unit
    ) {
        loggerList.forEach {
            if (it.isLoggable(severity)) {
                it.loggingCall(message, tag, throwable)
            }
        }
    }
}
