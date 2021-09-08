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

import co.touchlab.stately.concurrency.Lock
import co.touchlab.stately.concurrency.withLock

internal class LockMutableKermitConfig : MutableKermitConfig {
    private var _minSeverity: Severity = Severity.Debug
    private var _loggerList: List<LogWriter> = listOf(
        CommonWriter()
    )
    private var _defaultTag: String = "Kermit"
    private val lock = Lock()

    override var minSeverity: Severity
        get() = _minSeverity
        set(value) {
            lock.withLock {
                _minSeverity = value
            }
        }

    override var loggerList: List<LogWriter>
        get() = _loggerList
        set(value) {
            lock.withLock {
                _loggerList = value
            }
        }

    override var defaultTag: String
        get() = _defaultTag
        set(value) {
            lock.withLock {
                _defaultTag = value
            }
        }
}