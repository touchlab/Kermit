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

internal class JvmMutableLoggerConfig(logWriters: List<LogWriter>) : MutableLoggerConfig {
    @Volatile
    private var _minSeverity: Severity = DEFAULT_MIN_SEVERITY

    @Volatile
    private var _loggerList: List<LogWriter> = logWriters

    override var minSeverity: Severity
        get() = _minSeverity
        set(value) {
            synchronized(this) {
                _minSeverity = value
            }
        }

    override var logWriterList: List<LogWriter>
        get() = _loggerList
        set(value) {
            synchronized(this) {
                _loggerList = value
            }
        }
}