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

import android.util.Log

class LogcatLogger : Logger() {

    private fun getSeverity(severity: Severity) = when (severity) {
        Severity.Verbose -> 2
        Severity.Debug -> 3
        Severity.Info -> 4
        Severity.Warn -> 5
        Severity.Error -> 6
        Severity.Assert -> 7
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        Log.println(getSeverity(severity), tag, message)
        throwable?.let {
            Log.println(
                getSeverity(severity),
                tag,
                it.stackTraceToString()
            )
        }
    }

    override fun v(message: String, tag: String, throwable: Throwable?) {
        Log.v(tag, message, throwable)
    }

    override fun d(message: String, tag: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun i(message: String, tag: String, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }

    override fun w(message: String, tag: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun e(message: String, tag: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    override fun wtf(message: String, tag: String, throwable: Throwable?) {
        Log.wtf(tag, message, throwable)
    }
}