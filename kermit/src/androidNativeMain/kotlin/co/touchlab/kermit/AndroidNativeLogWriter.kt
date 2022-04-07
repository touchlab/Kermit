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

import platform.android.ANDROID_LOG_DEBUG
import platform.android.ANDROID_LOG_ERROR
import platform.android.ANDROID_LOG_FATAL
import platform.android.ANDROID_LOG_INFO
import platform.android.ANDROID_LOG_VERBOSE
import platform.android.ANDROID_LOG_WARN
import platform.android.__android_log_print

class AndroidNativeLogWriter : LogWriter() {

    private fun getSeverity(severity: Severity) = when (severity) {
        Severity.Verbose -> ANDROID_LOG_VERBOSE
        Severity.Debug -> ANDROID_LOG_DEBUG
        Severity.Info -> ANDROID_LOG_INFO
        Severity.Warn -> ANDROID_LOG_WARN
        Severity.Error -> ANDROID_LOG_ERROR
        Severity.Assert -> ANDROID_LOG_FATAL
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        __android_log_print(getSeverity(severity).toInt(), tag, message)
        throwable?.let {
            __android_log_print(getSeverity(severity).toInt(), tag, it.stackTraceToString())
        }
    }
}
