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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog

@OptIn(ExperimentalKermitApi::class)
@RunWith(RobolectricTestRunner::class)
class LogcatLoggerTest {

    companion object {
        private const val DEFAULT_TAG = "logger"
    }

    private val testConfig = TestConfig(
        minSeverity = Severity.Verbose,
        logWriterList = listOf(LogcatWriter()),
    )

    @Test
    fun logsToLogcatTest() {
        val logger = BaseLogger(testConfig)
        logger.logBlock(Severity.Debug, "tag", null) { "Message" }

        ShadowLog.getLogs().apply {
            assert(size > 0)
        }
    }

    @Test
    fun testingDefaultTag() {
        val message = "TestingTag"
        val logger = BaseLogger(config = testConfig)
        logger.logBlock(Severity.Error, DEFAULT_TAG, null) { message }

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
        val logger = BaseLogger(config = testConfig)
        logger.logBlock(Severity.Error, tag, null) { message }

        ShadowLog.getLogs().apply {
            assert(size > 0)
            val logsWithMessage = this.filter { it.msg == message }
            assertEquals(logsWithMessage.last().tag, tag)
        }
    }

    @Test
    fun testLogThrowable() {
        val logger = BaseLogger(testConfig)
        logger.log(
            severity = Severity.Error,
            tag = "Test Tag",
            throwable = RuntimeException(
                "Root Exception Message",
                IllegalStateException("Cause Exception Message"),
            ),
            message = "Error Message",
        )

        ShadowLog.getLogs().apply {
            assert(size > 0)
            assertNotNull(find { it?.throwable?.message?.contains("Root Exception Message") ?: false })
            assertNotNull(find { it?.throwable?.cause?.message?.contains("Cause Exception Message") ?: false })
        }
    }
}
