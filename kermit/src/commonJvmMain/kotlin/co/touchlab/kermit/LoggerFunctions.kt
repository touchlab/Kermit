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

inline fun Logger.v(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Verbose)
        log(Severity.Verbose, tag, throwable, message())
}

inline fun Logger.d(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Debug)
        log(Severity.Debug, tag, throwable, message())
}

inline fun Logger.i(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Info)
        log(Severity.Info, tag, throwable, message())
}

inline fun Logger.w(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Warn)
        log(Severity.Warn, tag, throwable, message())
}

inline fun Logger.e(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Error)
        log(Severity.Error, tag, throwable, message())
}

inline fun Logger.a(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (config.minSeverity <= Severity.Assert)
        log(Severity.Assert, tag, throwable, message())
}