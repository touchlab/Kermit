package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KermitTest {
    @Test
    fun `can config Kimber`() {
        val kimber = Kermit(testLogger)
        kimber.e{"Message"}
        assertTrue(logged)
    }

    @Test
    fun testIsLoggable(){
        val kermit = Kermit(testLogger)

        logged = false
        kermit.v { "Message" }
        assertFalse(logged)

        logged = false
        kermit.d { "Message" }
        assertFalse(logged)

        logged = false
        kermit.i { "Message" }
        assertFalse(logged)

        logged = false
        kermit.w { "Message" }
        assertTrue(logged)

        logged = false
        kermit.e { "Message" }
        assertTrue(logged)

        logged = false
        kermit.wtf { "Message" }
        assertTrue(logged)

    }

    @Test
    fun testMultipleLoggers() {
        logged = false
        secondaryLogged = false
        val kermit = Kermit(testLogger, secondaryTestLogger)
        kermit.e { "Message" }

        assertTrue(logged)
        assertTrue(secondaryLogged)
    }

    @Test
    fun testSingleLogger() {
        logged = false
        secondaryLogged = false
        val kermit = Kermit(secondaryTestLogger)
        kermit.e { "Message" }

        assertFalse(logged)
        assertTrue(secondaryLogged)
    }

    var logged = false
    private val testLogger = object : Logger() {

        override fun isLoggable(severity: Severity): Boolean {
            return (severity == Severity.Assert ||
                    severity == Severity.Error ||
                    severity == Severity.Warn)
        }

        override fun log(severity: Severity, message: String, tag: String?, throwable: Throwable?) {
            logged = true
        }
    }

    var secondaryLogged = false
    private val secondaryTestLogger = object : Logger() {

        override fun log(severity: Severity, message: String, tag: String?, throwable: Throwable?) {
            secondaryLogged = true
        }
    }
}