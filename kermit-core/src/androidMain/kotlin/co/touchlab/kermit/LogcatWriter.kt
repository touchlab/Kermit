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

class LogcatWriter : LogWriter() {
    private fun getSeverity(severity: Severity) = when (severity) {
        Severity.Verbose -> Log.VERBOSE
        Severity.Debug -> Log.DEBUG
        Severity.Info -> Log.INFO
        Severity.Warn -> Log.WARN
        Severity.Error -> Log.ERROR
        Severity.Assert -> Log.ASSERT
    }

    override fun isLoggable(tag: String, severity: Severity): Boolean {
        return Log.isLoggable(tag, getSeverity(severity))
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        if(throwable == null){
            when(severity){
                Severity.Verbose -> Log.v(tag, message)
                Severity.Debug -> Log.d(tag, message)
                Severity.Info -> Log.i(tag, message)
                Severity.Warn -> Log.w(tag, message)
                Severity.Error -> Log.e(tag, message)
                Severity.Assert -> Log.wtf(tag, message)
            }
        }else{
            when(severity){
                Severity.Verbose -> Log.v(tag, message, throwable)
                Severity.Debug -> Log.d(tag, message, throwable)
                Severity.Info -> Log.i(tag, message, throwable)
                Severity.Warn -> Log.w(tag, message, throwable)
                Severity.Error -> Log.e(tag, message, throwable)
                Severity.Assert -> Log.wtf(tag, message, throwable)
            }
        }
    }
}