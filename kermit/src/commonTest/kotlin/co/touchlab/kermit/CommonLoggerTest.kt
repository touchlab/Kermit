package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonLoggerTest {
    @Test
    fun commonLoggerThrowableTest() {
        var usedThrowableProvider = false
        val kermit = Kermit(CommonLogger(object : ThrowableStringProvider {
            override fun getThrowableString(throwable: Throwable): String {
                usedThrowableProvider = true
                return "Test Throwable String"
            }
        }))
        kermit.d(throwable = RuntimeException("My Exception")) { "Test" }

        assertTrue(usedThrowableProvider)
    }
}