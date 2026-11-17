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

    /**
     * Formats the [Throwable] attached to a log call. The result is appended to the formatted message on a new line, so the
     * message and its stack trace are written as a single log entry. Return an empty string to omit the throwable.
     */
    fun formatThrowable(throwable: Throwable) = throwable.stackTraceToString()

    @Deprecated(
        "Override or call the formatMessage overload that takes a Throwable, so the message and the throwable are formatted " +
            "as a single entry. This overload will be removed in a future release.",
        ReplaceWith("formatMessage(severity, tag, message, null)"),
    )
    fun formatMessage(severity: Severity?, tag: Tag?, message: Message): String {
        // Optimize for Android
        if (severity == null && tag == null) {
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
        }
    }

    /**
     * Formats a log entry. By default, this is the severity, tag, and message, followed by [formatThrowable] on a new line
     * when a [throwable] is present.
     */
    fun formatMessage(severity: Severity?, tag: Tag?, message: Message, throwable: Throwable?): String {
        // Delegates to the legacy overload so formatters that only override it keep working.
        @Suppress("DEPRECATION")
        val formattedMessage = formatMessage(severity, tag, message)
        if (throwable == null) return formattedMessage

        val formattedThrowable = formatThrowable(throwable)
        if (formattedThrowable.isEmpty()) return formattedMessage

        return "$formattedMessage\n$formattedThrowable"
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
    override fun formatMessage(severity: Severity?, tag: Tag?, message: Message, throwable: Throwable?): String =
        super.formatMessage(null, null, message, throwable)
}

/**
 * Same output as [DefaultFormatter], but never includes the [Throwable]'s stack trace in the formatted message.
 */
object NoThrowableFormatter : MessageStringFormatter {
    override fun formatThrowable(throwable: Throwable): String = ""
}
