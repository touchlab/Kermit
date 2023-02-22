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
        val osLogWriter = OSLogWriter(DefaultLogFormatter, collector)
        osLogWriter.log(Severity.Debug, "Hello", "ATag", null)
        assertEquals(collector.logs.size, 1)
        assertEquals(collector.logs[0].message, "(ATag) Hello")
    }
}

class DarwinLoggerCollector : DarwinLogger {
    val logs = mutableListOf<LogLine>()
    override fun log(osLogSeverity: UByte, message: String) {
        logs.add(LogLine(osLogSeverity, message))
    }
}

data class LogLine(val osLogSeverity: UByte, val message: String)