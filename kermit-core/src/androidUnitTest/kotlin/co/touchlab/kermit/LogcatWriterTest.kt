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
import kotlin.test.fail

/**
 * The [LogcatLoggerTest] class can run without failing as it uses the roboeletric runner, which produces the stubs.
 * In this sample, we run it without it to simulate the real user experience when they are not using roboeletric.
 */
class LogcatWriterTest {
    private val testConfig = TestConfig(
        minSeverity = Severity.Verbose,
        logWriterList = listOf(LogcatWriter()),
    )

    @Test
    fun logsToLogcat_withoutMocks_doNotCrash() {
        val logger = BaseLogger(testConfig)

        try {
            logger.logBlock(Severity.Debug, "tag", null) { "Message" }
        } catch (e: Throwable) {
            fail("Logcat call should crash in Android UnitTest mode", e)
        }
    }
}
