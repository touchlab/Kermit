/*
 * Copyright (c) 2025 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.crashlytics

import co.touchlab.crashkios.crashlytics.CrashlyticsCalls
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CrashlyticsLogWriterTest {
    @Test
    fun testCrashlyticsLogger_Equal() {
        val crashlyticsCalls = TestingCrashlyticsCalls()
        val logger = CrashlyticsTestingLogger(
            minSeverityLogger = Severity.Info,
            minSeverityLogWriter = Severity.Info,
            crashlyticsCalls
        )
        logger.writeTestingLogs()
        crashlyticsCalls.assertSeverity()
    }

    @Test
    fun testCrashlyticsLogger_Logger() {
        val crashlyticsCalls = TestingCrashlyticsCalls()
        val logger = CrashlyticsTestingLogger(
            minSeverityLogger = Severity.Info,
            minSeverityLogWriter = Severity.Verbose,
            crashlyticsCalls
        )
        logger.writeTestingLogs()
        crashlyticsCalls.assertSeverity()
    }

    @Test
    fun testCrashlyticsLogger_LogWriter() {
        val crashlyticsCalls = TestingCrashlyticsCalls()
        val logger = CrashlyticsTestingLogger(
            minSeverityLogger = Severity.Verbose,
            minSeverityLogWriter = Severity.Info,
            crashlyticsCalls
        )
        logger.writeTestingLogs()
        crashlyticsCalls.assertSeverity()
    }

    private fun Logger.writeTestingLogs() {
        e { "Error" }
        w { "Warning" }
        i { "Info" }
        d { "Debug" }
        v { "Verbose" }
    }

    private fun TestingCrashlyticsCalls.assertSeverity() {
        assertTrue(loggedMessage("Error"), "Min Severity is Info and this is Error")
        assertTrue(loggedMessage("Warning"), "Min Severity is Info and this is Warning")
        assertTrue(loggedMessage("Info"), "Min Severity is Info and this is Info")
        assertFalse(loggedMessage("Debug"), "Min Severity is Info and this is Debug")
        assertFalse(loggedMessage("Verbose"), "Min Severity is Info and this is Verbose")
    }
}

@OptIn(ExperimentalKermitApi::class)
private class CrashlyticsTestingLogger(
    minSeverityLogger: Severity,
    minSeverityLogWriter: Severity,
    crashlyticsCalls: CrashlyticsCalls,
) : Logger(
    config = loggerConfigInit(
        CrashlyticsLogWriter(
            minSeverity = minSeverityLogWriter,
            crashlyticsCalls = crashlyticsCalls
        ),
        minSeverity = minSeverityLogger,
    ),
    tag = "MyAppTag"
)

private class TestingCrashlyticsCalls : CrashlyticsCalls {

    private val loggedMessages = mutableListOf<String>()

    fun loggedMessage(message:String) = loggedMessages.any {
        // Need to ignore Severity and Tag
        it.endsWith(message)
    }

    override fun logMessage(message: String) {
        print("Logging Message")
        loggedMessages.add(message)
    }

    override fun sendFatalException(throwable: Throwable) {
        print("Sending Fatal Exception")
    }

    override fun sendHandledException(throwable: Throwable) {
        print("Sending Handled Exception")
    }

    override fun setCustomValue(key: String, value: Any) {
        print("Setting Custom Value")
    }

    override fun setUserId(identifier: String) {
        print("Setting User ID")
    }

}