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

import kotlin.jvm.JvmInline

/**
 * Implement message format for log writer. Some platforms have native support for either severity or tags. A formatter
 * should ignore those parameters for relevant platforms. For example, on Android both tag and severity are part of the
 * logging API. However, in JavaScript, while severity is part of the API, tags are not.
 */
interface LogFormatter {
    fun formatSeverity(severity: Severity) = "$severity:"
    fun formatTag(tag: Tag) = "(${tag.tag})"
    fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String {
        val sb = StringBuilder()
        if (severity != null) sb.append(formatSeverity(severity)).append(" ")
        if (tag != null) sb.append(formatTag(tag)).append(" ")
        sb.append(message.message)
        return sb.toString()
    }
}

@JvmInline
value class Tag(internal val tag:String)
@JvmInline
value class Message(internal val message: String)

/**
 * Single-line string with severity, tag, and message.
 *
 * Empty tags will omit the prefix space and parenthesis.
 *
 * "[severity: ][(tag) ][message]"
 */
object DefaultLogFormatter:LogFormatter

/**
 * Tags are a formal part of Android, but not other systems. This formatter omits them.
 */
object NoTagLogFormatter:LogFormatter {
    override fun formatTag(tag: Tag): String = ""
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String = super.formatMessage(severity, null, message)
}

/**
 * Just logs the message.
 */
object SimpleLogFormatter: LogFormatter {
    override fun formatTag(tag: Tag): String = ""
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String = message.message
}
