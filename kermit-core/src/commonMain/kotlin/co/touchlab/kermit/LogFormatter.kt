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

interface LogFormatter {
    fun formatTag(tag: String) = if(tag.isEmpty()){""}else{" ($tag)"}
    fun formatMessage(severity: Severity, message: Message, tag: Tag): String = "$severity:${formatTag(tag)} $message"
}

typealias Tag = String
typealias Message = String

/**
 * Single-line string with severity, tag, and message.
 *
 * Empty tags will omit the prefix space and parenthesis.
 *
 * "[severity]:[ (tag)|] [message]"
 */
object DefaultLogFormatter:LogFormatter

/**
 * Tags are a formal part of Android, but not other systems. This formatter omits them.
 */
object NoTagLogFormatter:LogFormatter {
    override fun formatTag(tag: String): String = ""
    override fun formatMessage(severity: Severity, message: Message, tag: Tag): String = "$severity: $message"
}

/**
 * Just logs the message.
 */
object SimpleLogFormatter: LogFormatter {
    override fun formatTag(tag: String): String = ""
    override fun formatMessage(severity: Severity, message: Message, tag: Tag): String = message
}
