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
}