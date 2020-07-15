/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

import kotlin.test.Test

class KermitTest {
    private val testLogger = TestLogger()

    @Test
    fun simpleLogTest() {
        val kimber = Kermit(testLogger)
        kimber.e { "Message" }
        testLogger.assertCount(1)
    }

    @Test
    fun testIsLoggable() {
        val errorLogger = TestLogger(Severity.Error)
        val kermit = Kermit(errorLogger)

        kermit.v { "verbose" }
        kermit.d { "debug" }
        kermit.i { "info" }
        kermit.w { "warn" }

        testLogger.assertCount(0)
        kermit.e { "error" }
        errorLogger.assertLast { message == "error" && severity == Severity.Error }

        kermit.wtf { "wtf" }
        errorLogger.assertCount(2)
        errorLogger.assertLast { message == "wtf" && severity == Severity.Assert }
    }

    @Test
    fun testMultipleLoggers() {
        val secondaryLogger = TestLogger()
        val kermit = Kermit(testLogger, secondaryLogger)
        kermit.e { "Message" }

        testLogger.assertLast { message == "Message" }
        secondaryLogger.assertLast { message == "Message" }
    }

    @Test
    fun testSingleLogger() {
        val secondaryLogger = TestLogger()
        val kermit = Kermit(testLogger)
        kermit.e { "Message" }

        testLogger.assertCount(1)
        secondaryLogger.assertCount(0)
    }

    @Test
    fun testLogParameters() {
        val kermit = Kermit(testLogger)
        val testTag = "TESTTAG"
        val testMessage = "This Is My Message"
        kermit.e(testTag) { testMessage }
        testLogger.assertLast { tag == testTag && message == testMessage }
    }

    @Test
    fun testingDefaultTag() {
        val kermit = Kermit(testLogger)
        val kermitWithTag = kermit.withTag("My Custom Tag")

        kermit.i("MainActivity") { "onCreate" }
        testLogger.assertLast { tag == "MainActivity" }
        kermit.d { "Log Without Tag (Original Kermit)" }

        testLogger.assertLast { tag == "Kermit" }
        kermitWithTag.d { "Log Without Tag (Kermit With Tag)" }

        testLogger.assertLast { tag == "My Custom Tag" }
        kermit.d("Tag") { "Log WITH Tag (Original Kermit" }
        testLogger.assertLast { tag == "Tag" }
        kermit.d { "Log Without Tag (Original Kermit)" } // Ensuring first Kermit isn't affected by withTag
        testLogger.assertLast { tag == "Kermit" }
    }
}
