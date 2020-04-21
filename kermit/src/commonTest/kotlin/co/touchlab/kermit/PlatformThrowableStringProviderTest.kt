package co.touchlab.kermit

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlatformThrowableStringProviderTest {
    @Test
    fun printsCauseTest() {
        val provider = PlatformThrowableStringProvider()

        val throwable = RuntimeException(
            "Root Exception Message",
            IllegalStateException("Cause Exception Message")
        )

        provider.getThrowableString(throwable).apply {
            assertTrue(contains("RuntimeException"))
            assertTrue(contains("Root Exception Message"))
            assertTrue(contains("IllegalStateException"))
            assertTrue(contains("Cause Exception Message"))
        }
    }

    @Test
    fun defaultImplTest() {
        val provider = object : ThrowableStringProvider {}
        val throwable = RuntimeException(
            "Root Exception Message", IllegalStateException(
                "cause 1",
                IllegalStateException(
                    "cause 2",
                    IllegalStateException(
                        "cause 3",
                        IllegalStateException(
                            "cause 4",
                            IllegalStateException("cause 5")
                        )
                    )
                )
            )
        )

        provider.getThrowableString(throwable).apply {
            assertTrue(contains("RuntimeException"))
            assertTrue(contains("Root Exception Message"))
            assertTrue(contains("IllegalStateException"))
            assertTrue(contains("cause 1"))
            assertTrue(contains("cause 2"))
            assertTrue(contains("cause 3"))

            assertFalse(contains("cause 4"))
            assertFalse(contains("cause 5"))
        }
    }
}
