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
interface MessageStringFormatter {
    fun formatSeverity(severity: Severity) = "$severity:"
    fun formatTag(tag: Tag) = "(${tag.tag})"

    fun formatThrowable(throwable: Throwable) = throwable.stackTraceToString()

    @Deprecated("Keeping for binary compatibility", level = DeprecationLevel.HIDDEN)
    fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String = formatMessage(severity, tag, message, null)

    fun formatMessage(severity: Severity?, tag: Tag?, message: Message, throwable: Throwable?): String {
        // Optimize for Android
        if (severity == null && tag == null && throwable == null) {
            return message.message
        }

        return buildString {
            if (severity != null) {
                val formattedSeverity = formatSeverity(severity)
                if (formattedSeverity.isNotEmpty()) append(formattedSeverity).append(" ")
            }

            if (tag != null && tag.tag.isNotEmpty()) {
                val formattedTag = formatTag(tag)
                if (formattedTag.isNotEmpty()) append(formattedTag).append(" ")
            }

            append(message.message)

            if (throwable != null) {
                val formattedThrowable = formatThrowable(throwable)
                if (formattedThrowable.isNotEmpty()) appendLine().append(formattedThrowable)
            }
        }
    }
}

@JvmInline
value class Tag(val tag: String)

@JvmInline
value class Message(val message: String)

/**
 * Single-line string with severity, tag, and message.
 *
 * Empty tags will omit the prefix space and parenthesis.
 *
 * "[severity: ][(tag) ][message]"
 */
object DefaultFormatter : MessageStringFormatter

/**
 * Tags are a formal part of Android, but not other systems. This formatter omits them.
 */
object NoTagFormatter : MessageStringFormatter {
    override fun formatTag(tag: Tag): String = ""
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message, throwable: Throwable?): String =
        super.formatMessage(severity, null, message, throwable)
}

/**
 * Just logs the message.
 */
object SimpleFormatter : MessageStringFormatter {
    override fun formatTag(tag: Tag): String = ""
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message, throwable: Throwable?): String = message.message
}

/**
 *
 */
object NoThrowableFormatter : MessageStringFormatter {
    override fun formatThrowable(throwable: Throwable): String = ""
}
