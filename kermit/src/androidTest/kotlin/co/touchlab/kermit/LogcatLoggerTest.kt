package co.touchlab.kermit

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.test.Test

@Suppress("IllegalIdentifier")
@RunWith(RobolectricTestRunner::class)
class LogcatLoggerTest {
    @Test
    fun `Logs to logcat`() {
        val kermit = Kermit(LogcatLogger())
        kermit.e{"Message"}

        ShadowLog.getLogs().apply {
            assert(size > 0)
            forEach { println(it) }
        }
    }
}

