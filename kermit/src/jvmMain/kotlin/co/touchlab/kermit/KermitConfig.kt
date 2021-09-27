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

private val lock = Any()

@Volatile
private var internalDefaultTag: String = DEFAULT_TAG

internal actual fun mutableKermitConfigInit(logWriters: List<LogWriter>): MutableLoggerConfig = JvmMutableLoggerConfig(logWriters)

internal actual var defaultTag: String
    get() = internalDefaultTag
    set(value) {
        synchronized(lock) {
            internalDefaultTag = value
        }
    }