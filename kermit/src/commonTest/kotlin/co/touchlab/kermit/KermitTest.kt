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

class KermitTest {
    private val testLogger = TestLogWriter(loggable = Severity.Verbose)
    private val testConfig = TestConfig(
        minSeverity = Severity.Verbose,
        logWriterList = listOf(testLogger),
        defaultTag = "Kermit"
    )

    @Test
    fun defaultConfigTest() {
        val logger = Logger(LoggerConfig.default.copy(logWriterList = listOf(testLogger)))
        logger.v { "Message" }
        testLogger.assertCount(1)
    }

    @Test
    fun configSeverityCheckFailedTest() {
        val logger = Logger(testConfig.copy(minSeverity = Severity.Error))
        logger.v { "Message" }
        testLogger.assertCount(0)
    }

    @Test
    fun simpleLogTest() {
        val logger = Logger(testConfig)
        logger.e { "Message" }
        testLogger.assertCount(1)
    }

    @Test
    fun directLogTest() {
        val logger = Logger(testConfig)
        logger.v("Message")
        logger.d("Message")
        logger.i("Message")
        logger.w("Message")
        logger.e("Message")
        logger.a("Message")
        testLogger.assertCount(6)
    }

    @Test
    fun testIsLoggable() {
        val errorLogger = TestLogWriter(Severity.Error)
        val logger = Logger(testConfig.copy(logWriterList = listOf(errorLogger)))

        logger.v { "verbose" }
        logger.d { "debug" }
        logger.i { "info" }
        logger.w { "warn" }

        errorLogger.assertCount(0)
        logger.e { "error" }
        errorLogger.assertLast { message == "error" && severity == Severity.Error }

        logger.a { "assert" }
        errorLogger.assertCount(2)
        errorLogger.assertLast { message == "assert" && severity == Severity.Assert }
    }

    @Test
    fun testMultipleLoggers() {
        val secondaryLogger = TestLogWriter(loggable = Severity.Verbose)
        val loggerList = listOf(testLogger, secondaryLogger)
        val logger = Logger(testConfig.copy(logWriterList = loggerList))
        logger.e { "Message" }

        testLogger.assertLast { message == "Message" }
        secondaryLogger.assertLast { message == "Message" }
    }

    @Test
    fun testSingleLogger() {
        val secondaryLogger = TestLogWriter(loggable = Severity.Verbose)
        val logger = Logger(testConfig)
        logger.e { "Message" }

        testLogger.assertCount(1)
        secondaryLogger.assertCount(0)
    }

    @Test
    fun testingDefaultTag() {
        val logger = Logger(testConfig)
        val loggerWithTag = logger.withTag("My Custom Tag")

        logger.d { "Log Without Tag (Original Kermit)" }
        testLogger.assertLast { tag == "Kermit" }

        loggerWithTag.d { "Log Without Tag (Kermit With Tag)" }
        testLogger.assertLast { tag == "My Custom Tag" }

        logger.d { "Log Without Tag (Original Kermit)" }  // Ensuring first Kermit isn't affected by withTag
        testLogger.assertLast { tag == "Kermit" }
    }
}