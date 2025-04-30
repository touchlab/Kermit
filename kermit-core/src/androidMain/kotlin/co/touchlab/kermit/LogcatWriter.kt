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

import android.util.Log

class LogcatWriter(private val messageStringFormatter: MessageStringFormatter = DefaultFormatter) : LogWriter() {
    // When running unit tests, Log calls will fail. Back up to a common writer
    private val testWriter: CommonWriter = CommonWriter(messageStringFormatter)

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val formattedMessage = messageStringFormatter.formatMessage(null, null, Message(message))
        try {
            if (throwable == null) {
                when (severity) {
                    Severity.Verbose -> Log.v(tag, formattedMessage)
                    Severity.Debug -> Log.d(tag, formattedMessage)
                    Severity.Info -> Log.i(tag, formattedMessage)
                    Severity.Warn -> Log.w(tag, formattedMessage)
                    Severity.Error -> Log.e(tag, formattedMessage)
                    Severity.Assert -> Log.wtf(tag, formattedMessage)
                }
            } else {
                when (severity) {
                    Severity.Verbose -> Log.v(tag, formattedMessage, throwable)
                    Severity.Debug -> Log.d(tag, formattedMessage, throwable)
                    Severity.Info -> Log.i(tag, formattedMessage, throwable)
                    Severity.Warn -> Log.w(tag, formattedMessage, throwable)
                    Severity.Error -> Log.e(tag, formattedMessage, throwable)
                    Severity.Assert -> Log.wtf(tag, formattedMessage, throwable)
                }
            }
        } catch (e: Exception) {
            testWriter.log(severity, message, tag, throwable)
        }
    }
}
