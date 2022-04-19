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

import kotlin.test.Test
import kotlin.test.assertEquals

class OSLogWriterTest {
    @Test
    fun testFormatMessage() {
        val testOSLogWriter = TestOSLogWriter()
        val l = Logger(StaticConfig(logWriterList = listOf(testOSLogWriter)), "my tag")

        l.d { "some logging" }

        assertEquals("Debug: (my tag) some logging", testOSLogWriter.logList[0])
    }

    internal class TestOSLogWriter:OSLogWriter(){
        val logList = mutableListOf<String>()
        override fun callLog(severity: Severity, message: String, throwable: Throwable?) {
            logList.add(message)
        }
    }
}