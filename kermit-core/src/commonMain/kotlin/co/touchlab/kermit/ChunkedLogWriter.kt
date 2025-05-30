/*
 * Copyright (c) 2024 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

class ChunkedLogWriter(internal val wrapped: LogWriter, private val maxMessageLength: Int, private val minMessageLength: Int) :
    LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        chunkedLog(severity, message, tag, throwable)
    }

    private tailrec fun chunkedLog(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        if (message.length > maxMessageLength) {
            var msgSubstring = message.substring(0, maxMessageLength)
            var msgSubstringEndIndex = maxMessageLength

            // Try to find a substring break at a newline char.
            msgSubstring.lastIndexOf('\n').let { lastIndex ->
                if (lastIndex >= minMessageLength) {
                    msgSubstring = msgSubstring.substring(0, lastIndex)
                    // skip over new line char
                    msgSubstringEndIndex = lastIndex + 1
                }
            }

            // Log the substring.
            wrapped.log(severity, msgSubstring, tag, throwable)

            // Recursively log the remainder.
            chunkedLog(severity, message.substring(msgSubstringEndIndex), tag, throwable)
        } else {
            wrapped.log(severity, message, tag, throwable)
        }
    }
}

fun LogWriter.chunked(maxMessageLength: Int = 4000, minMessageLength: Int = 3000): LogWriter =
    ChunkedLogWriter(this, maxMessageLength, minMessageLength)
