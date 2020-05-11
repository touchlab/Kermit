package co.touchlab.kermit

import java.io.PrintWriter
import java.io.StringWriter

actual class PlatformThrowableStringProvider : ThrowableStringProvider {
    override fun getThrowableString(throwable: Throwable): String {
        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        return sw.toString()
    }
}