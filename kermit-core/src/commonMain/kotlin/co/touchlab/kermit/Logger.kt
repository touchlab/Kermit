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

open class Logger(
    val config:LoggerConfig,
    val tag: String = config.defaultTag
) {
    fun withTag(tag: String): Logger {
        return Logger(this.config, tag)
    }

    /**
     * We could reduce the methods on this object, but the native objc
     * export can't use default arguments, so we have a few extra methods defined.
     */
    inline fun v(message: () -> String){
        if(config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, null, message())
    }

    inline fun v(throwable: Throwable, message: () -> String){
        if(config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, throwable, message())
    }

    fun v(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Verbose)
            log(Severity.Verbose, tag, throwable, message)
    }

    inline fun d(message: () -> String){
        if(config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, null, message())
    }

    inline fun d(throwable: Throwable, message: () -> String) {
        if(config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, throwable, message())
    }

    fun d(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Debug)
            log(Severity.Debug, tag, throwable, message)
    }

    inline fun i(message: () -> String){
        if(config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, null, message())
    }

    inline fun i(throwable: Throwable, message: () -> String) {
        if(config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, throwable, message())
    }

    fun i(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Info)
            log(Severity.Info, tag, throwable, message)
    }

    inline fun w(message: () -> String){
        if(config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, null, message())
    }

    inline fun w(throwable: Throwable, message: () -> String) {
        if(config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, throwable, message())
    }

    fun w(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Warn)
            log(Severity.Warn, tag, throwable, message)
    }

    inline fun e(message: () -> String){
        if(config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, null, message())
    }

    inline fun e(throwable: Throwable, message: () -> String) {
        if(config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, throwable, message())
    }

    fun e(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Error)
            log(Severity.Error, tag, throwable, message)
    }

    inline fun a(message: () -> String){
        if(config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, null, message())
    }

    inline fun a(throwable: Throwable, message: () -> String) {
        if(config.minSeverity <= Severity.Assert)
            log(Severity.Assert, tag, throwable, message())
    }

    fun a(message: String, throwable: Throwable? = null){
        if(config.minSeverity <= Severity.Assert)
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
        config.loggerList.forEach {
            if (it.isLoggable(severity)) {
                it.loggingCall(message, tag, throwable)
            }
        }
    }
}

