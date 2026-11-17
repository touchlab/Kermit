/*
 * Copyright (c) 2023 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertEquals

class OSLogWriterTest {
    @Test
    fun basicLog() {
        val collector = DarwinLoggerCollector()
        val osLogWriter = OSLogWriter(DefaultFormatter, collector)
        osLogWriter.log(Severity.Debug, "Hello", "ATag", null)
        assertEquals(collector.logs.size, 1)
        assertEquals(collector.logs[0].message, "(ATag) Hello")
    }

    @Test
    fun throwableIsLoggedInSameEntry() {
        val collector = DarwinLoggerCollector()
        val osLogWriter = OSLogWriter(DefaultFormatter, collector)
        val throwable = RuntimeException("Boom")
        osLogWriter.log(Severity.Error, "Hello", "ATag", throwable)
        assertEquals(1, collector.logs.size)
        assertEquals("(ATag) Hello\n${throwable.stackTraceToString()}", collector.logs[0].message)
    }

    @Test
    fun legacyCallLogOverrideIsStillCalled() {
        val collector = DarwinLoggerCollector()
        val calls = mutableListOf<String>()
        val osLogWriter = object : OSLogWriter(DefaultFormatter, collector) {
            @Deprecated("Testing the legacy overload")
            override fun callLog(severity: Severity, message: String, throwable: Throwable?) {
                calls.add(message)
                @Suppress("DEPRECATION")
                super.callLog(severity, message, throwable)
            }
        }
        osLogWriter.log(Severity.Info, "Hello", "ATag", null)
        assertEquals(listOf("(ATag) Hello"), calls)
        assertEquals(1, collector.logs.size)
    }
}

class DarwinLoggerCollector : DarwinLogger {
    val logs = mutableListOf<LogLine>()
    override fun log(osLogSeverity: UByte, message: String) {
        logs.add(LogLine(osLogSeverity, message))
    }
}

data class LogLine(val osLogSeverity: UByte, val message: String)
