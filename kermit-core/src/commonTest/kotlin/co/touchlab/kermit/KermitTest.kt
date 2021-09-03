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
    private val testLogger = TestLogger(loggable = Severity.Verbose)
    private val testConfig = TestConfig(
        minSeverity = Severity.Verbose,
        loggerList = listOf(testLogger),
        defaultTag = "Kermit"
    )

    @Test
    fun configSeverityCheckFailedTest() {
        val kermit = KermitInstance(testConfig.copy(minSeverity = Severity.Error))
        kermit.v { "Message" }
        testLogger.assertCount(0)
    }

    @Test
    fun simpleLogTest() {
        val kermit = KermitInstance(testConfig)
        kermit.e { "Message" }
        testLogger.assertCount(1)
    }

    @Test
    fun directLogTest() {
        val kermit = KermitInstance(testConfig)
        kermit.v("Message")
        kermit.d("Message")
        kermit.i("Message")
        kermit.w("Message")
        kermit.e("Message")
        kermit.a("Message")
        testLogger.assertCount(6)
    }

    @Test
    fun testIsLoggable() {
        val errorLogger = TestLogger(Severity.Error)
        val kermit = KermitInstance(testConfig.copy(loggerList = listOf(errorLogger)))

        kermit.v { "verbose" }
        kermit.d { "debug" }
        kermit.i { "info" }
        kermit.w { "warn" }

        errorLogger.assertCount(0)
        kermit.e { "error" }
        errorLogger.assertLast { message == "error" && severity == Severity.Error }

        kermit.a { "assert" }
        errorLogger.assertCount(2)
        errorLogger.assertLast { message == "assert" && severity == Severity.Assert }
    }

    @Test
    fun testMultipleLoggers() {
        val secondaryLogger = TestLogger(loggable = Severity.Verbose)
        val loggerList = listOf(testLogger, secondaryLogger)
        val kermit = KermitInstance(testConfig.copy(loggerList = loggerList))
        kermit.e { "Message" }

        testLogger.assertLast { message == "Message" }
        secondaryLogger.assertLast { message == "Message" }
    }

    @Test
    fun testSingleLogger() {
        val secondaryLogger = TestLogger(loggable = Severity.Verbose)
        val kermit = KermitInstance(testConfig)
        kermit.e { "Message" }

        testLogger.assertCount(1)
        secondaryLogger.assertCount(0)
    }

    @Test
    fun testingDefaultTag() {
        val kermit = KermitInstance(testConfig)
        val kermitWithTag = kermit.withTag("My Custom Tag")

        kermit.d { "Log Without Tag (Original Kermit)" }
        testLogger.assertLast { tag == "Kermit" }

        kermitWithTag.d { "Log Without Tag (Kermit With Tag)" }
        testLogger.assertLast { tag == "My Custom Tag" }

        kermit.d { "Log Without Tag (Original Kermit)" }  // Ensuring first Kermit isn't affected by withTag
        testLogger.assertLast { tag == "Kermit" }
    }
}
