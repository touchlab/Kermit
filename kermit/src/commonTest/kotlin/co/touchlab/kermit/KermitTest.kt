package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class KermitTest {
    @Test
    fun `can config Kimber`() {
        val kimber = Kermit(testLogger)
        kimber.e{"Message"}
        assertTrue(logged)
    }

    @Test
    fun testingDefaultTag(){
        val kermit = Kermit(testLogger)
        val kermitWithTag = kermit.withTag("My Custom Tag")

        kermit.i("MainActivity") { "onCreate" }
        assertEquals(currentTag,"MainActivity")
        kermit.d { "Log Without Tag (Original Kermit)" }
        assertEquals(currentTag,"Kermit")
        kermitWithTag.d { "Log Without Tag (Kermit With Tag)" }
        assertEquals(currentTag,"My Custom Tag")
        kermit.d("Tag") { "Log WITH Tag (Original Kermit" }
        assertEquals(currentTag,"Tag")
        kermit.d { "Log Without Tag (Original Kermit)" }  // Ensuring first Kermit isn't affected by withTag
        assertEquals(currentTag,"Kermit")
    }

    var logged = false
    var currentTag:String = ""

    private val testLogger = object : Logger() {
        override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
            logged = true
            currentTag = tag
        }
    }
}
