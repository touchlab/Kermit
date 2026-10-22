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

import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalKermitApi
data class TestConfig(override val minSeverity: Severity, override val logWriterList: List<LogWriter>) : LoggerConfig

@ExperimentalKermitApi
@OptIn(ExperimentalAtomicApi::class)
class TestLogWriter(private val loggable: Severity) : LogWriter() {
    @ExperimentalKermitApi
    data class LogEntry(val severity: Severity, val message: String, val tag: String?, val throwable: Throwable?)

    private val _logs = AtomicReference<List<LogEntry>>(emptyList())
    val logs: List<LogEntry>
        get() = _logs.load()

    fun assertCount(count: Int) {
        assertEquals(count, logs.size)
    }

    fun assertLast(check: LogEntry.() -> Boolean) {
        assertTrue(logs.last().check())
    }

    fun reset() {
        _logs.store(emptyList())
    }

    override fun isLoggable(tag: String, severity: Severity): Boolean = severity.ordinal >= loggable.ordinal

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val entry = LogEntry(severity, message, tag, throwable)
        while (true) {
            val current = _logs.load()
            if (_logs.compareAndSet(current, current + entry)) return
        }
    }
}
