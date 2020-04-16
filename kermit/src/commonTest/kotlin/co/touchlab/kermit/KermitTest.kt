package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertTrue

class KermitTest {
    @Test
    fun `can config Kermit`() {
        val kermit = Kermit(testLogger)
        kermit.e{"Message"}
        assertTrue(logged)
    }

    var logged = false
    private val testLogger = object : Logger() {
        override fun log(severity: Severity, message: String, tag: String?, throwable: Throwable?) {
            logged = true
        }
    }
}