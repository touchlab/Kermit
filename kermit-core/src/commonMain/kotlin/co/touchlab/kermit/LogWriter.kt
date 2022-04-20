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

abstract class LogWriter{
    open fun isLoggable(severity: Severity): Boolean = true

    abstract fun log(severity: Severity, message: String, tag: String, throwable: Throwable? = null)

    open fun v(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Verbose, message, tag, throwable)

    open fun d(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Debug, message, tag, throwable)

    open fun i(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Info, message, tag, throwable)

    open fun w(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Warn, message, tag, throwable)

    open fun e(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Error, message, tag, throwable)

    open fun a(message: String, tag: String, throwable: Throwable? = null) =
        log(Severity.Assert, message, tag, throwable)
}