/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

/**
 * Development-focused LogWriter. Will write a colored emoji according to Severity. The Throwable stack trace is written
 * to OSLog in the same entry as the message, so very long stack traces may be truncated by OSLog.
 */
open class XcodeSeverityWriter(private val messageStringFormatter: MessageStringFormatter = DefaultFormatter) :
    OSLogWriter(messageStringFormatter) {
    override fun formatMessage(severity: Severity, tag: Tag, message: Message, throwable: Throwable?): String =
        "${emojiPrefix(severity)} ${messageStringFormatter.formatMessage(null, tag, message, throwable)}"

    // If this looks familiar, yes, it came directly from Napier :) https://github.com/AAkira/Napier#darwinios-macos-watchos-tvosintelapple-silicon
    open fun emojiPrefix(severity: Severity): String = when (severity) {
        Severity.Verbose -> "⚪️"
        Severity.Debug -> "🔵"
        Severity.Info -> "🟢"
        Severity.Warn -> "🟡"
        Severity.Error -> "🔴"
        Severity.Assert -> "🟤️"
    }
}
