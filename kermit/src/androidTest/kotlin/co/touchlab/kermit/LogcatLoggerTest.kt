package co.touchlab.kermit

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.test.Test
import kotlin.test.assertEquals

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

    @Test
    fun testingDefaultTag() {
        val defaultTag = "Kermit"
        val message = "TestingTag"
        val kermit = Kermit(LogcatLogger())
        kermit.e{message}

        ShadowLog.getLogs().apply {
            assert(size > 0)
            val logsWithMessage = this.filter { it.msg == message }
            assertEquals(logsWithMessage.last().tag, defaultTag)
        }
    }

    @Test
    fun testingCustomTags() {
        val tag = "MyCustomTag"
        val message = "TestingCustomTag"
        val kermit = Kermit(LogcatLogger(tag))
        kermit.e{message}

        ShadowLog.getLogs().apply {
            assert(size > 0)
            val logsWithMessage = this.filter { it.msg == message }
            assertEquals(logsWithMessage.last().tag, tag)
        }
    }
}

