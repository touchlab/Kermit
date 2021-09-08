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

import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

internal class AtomicMutableLoggerConfig : MutableLoggerConfig {
    private val _minSeverity = AtomicReference(Severity.Debug)
    private val _loggerList = AtomicReference<List<LogWriter>>(listOf(CommonWriter()))
    private val _defaultTag = AtomicReference<String>("Kermit")

    override var minSeverity: Severity
        get() = _minSeverity.value
        set(value) {
            _minSeverity.value = value
        }
    override var loggerList: List<LogWriter>
        get() = _loggerList.value
        set(value) {
            _loggerList.value = value.freeze()
        }
    override var defaultTag: String
        get() = _defaultTag.value
        set(value) {
            _defaultTag.value = value
        }
}

internal val isStrictMemoryModel = Platform.memoryModel == MemoryModel.STRICT
internal actual fun mutableKermitConfigInit(): MutableLoggerConfig = if (isStrictMemoryModel) {
    AtomicMutableLoggerConfig()
} else {
    LockMutableLoggerConfig()
}