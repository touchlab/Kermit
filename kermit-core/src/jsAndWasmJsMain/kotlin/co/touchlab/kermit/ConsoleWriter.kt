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

@file:Suppress("unused")

package co.touchlab.kermit

class ConsoleWriter internal constructor(
    private val messageStringFormatter: MessageStringFormatter,
    private val console: ConsoleIntf
) : LogWriter() {
    constructor(messageStringFormatter: MessageStringFormatter = DefaultFormatter) : this(
        messageStringFormatter,
        ConsoleActual
    )

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        var output = messageStringFormatter.formatMessage(
            null,
            Tag(tag),
            Message(message)
        ) // Ignore severity. It is part of the console API.
        throwable?.let {
            output += " ${it.stackTraceToString()}"
        }
        when (severity) {
            Severity.Assert, Severity.Error -> console.error(output)
            Severity.Warn -> console.warn(output)
            Severity.Info -> console.info(output)
            Severity.Debug, Severity.Verbose -> console.log(output)
        }
    }
}

internal interface ConsoleIntf {
    fun error(output: String)
    fun warn(output: String)
    fun info(output: String)
    fun log(output: String)
}

internal expect object ConsoleActual : ConsoleIntf {
    override fun error(output: String)
    override fun warn(output: String)
    override fun info(output: String)
    override fun log(output: String)
}