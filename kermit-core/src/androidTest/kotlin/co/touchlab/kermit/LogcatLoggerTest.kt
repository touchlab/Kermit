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

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class LogcatLoggerTest {

    companion object {
        private const val DEFAULT_TAG = "Kermit"
    }

    private val testConfig = TestConfig(
        minSeverity = Severity.Verbose,
        loggerList = listOf(LogcatLogger()),
        defaultTag = DEFAULT_TAG
    )

    @Test
    fun logsToLogcatTest() {
        val kermit = KermitInstance(testConfig)
        kermit.e { "Message" }

        ShadowLog.getLogs().apply {
            assert(size > 0)
        }
    }

    @Test
    fun testingDefaultTag() {
        val message = "TestingTag"
        val kermit = KermitInstance(config = testConfig, tag = DEFAULT_TAG)
        kermit.e { message }

        ShadowLog.getLogs().apply {
            assert(size > 0)
            val logsWithMessage = this.filter { it.msg == message }
            assertEquals(logsWithMessage.last().tag, DEFAULT_TAG)
        }
    }

    @Test
    fun testingCustomTags() {
        val tag = "MyCustomTag"
        val message = "TestingCustomTag"
        val kermit = KermitInstance(config = testConfig, tag = tag)
        kermit.e { message }

        ShadowLog.getLogs().apply {
            assert(size > 0)
            val logsWithMessage = this.filter { it.msg == message }
            assertEquals(logsWithMessage.last().tag, tag)
        }
    }

    @Test
    fun testLogThrowable() {
        val kermit = KermitInstance(testConfig)
        kermit.log(
            severity = Severity.Error,
            tag = "Test Tag",
            throwable = RuntimeException(
                "Root Exception Message",
                IllegalStateException("Cause Exception Message")
            ),
            message = "Error Message"
        )

        ShadowLog.getLogs().apply {
            assert(size > 0)
            assertNotNull(find { it.msg.contains("Root Exception Message") })
            assertNotNull(find { it.msg.contains("Cause Exception Message") })
        }
    }
}