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

typealias MessageBlock = () -> String

/**
 * Base class for public Logger API. Extend to implement your own logger API.
 */
@Suppress("unused")
open class BaseLogger(
    open val config: LoggerConfig,
) {
    val mutableConfig: MutableLoggerConfig
        get() = config.let {
            if (it !is MutableLoggerConfig) {
                throw IllegalStateException("Logger config is not mutable")
            }
            it
        }

    inline fun logBlock(
        severity: Severity,
        tag: String,
        throwable: Throwable?,
        message: MessageBlock
    ) {
        if (config.minSeverity <= severity) {
            processLog(
                severity,
                tag,
                throwable,
                message()
            )
        }
    }

    inline fun log(
        severity: Severity,
        tag: String,
        throwable: Throwable?,
        message: String
    ) {
        if (config.minSeverity <= severity) {
            processLog(
                severity,
                tag,
                throwable,
                message
            )
        }
    }

    fun processLog(
        severity: Severity,
        tag: String,
        throwable: Throwable?,
        message: String
    ) {
        config.logWriterList.forEach {
            if (it.isLoggable(tag, severity)) {
                it.log(severity, message, tag, throwable)
            }
        }
    }
}

@kotlin.native.concurrent.SharedImmutable
internal val DEFAULT_MIN_SEVERITY = Severity.Verbose