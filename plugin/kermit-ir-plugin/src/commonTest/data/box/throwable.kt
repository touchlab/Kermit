// STRIP_BELOW: Info
// FILE: Logger.kt
package co.touchlab.kermit

open class Logger {
    inline fun v(message: () -> String) { message() }
    inline fun v(throwable: Throwable, message: () -> String) { message() }
    inline fun d(message: () -> String) { message() }
    inline fun d(throwable: Throwable, message: () -> String) { message() }
    inline fun i(message: () -> String) { message() }
    inline fun i(throwable: Throwable, message: () -> String) { message() }
    inline fun w(message: () -> String) { message() }
    inline fun w(throwable: Throwable, message: () -> String) { message() }
    inline fun e(message: () -> String) { message() }
    inline fun e(throwable: Throwable, message: () -> String) { message() }
    inline fun a(message: () -> String) { message() }
    inline fun a(throwable: Throwable, message: () -> String) { message() }

    fun withTag(tag: String): Logger = this

    companion object : Logger()
}

// FILE: main.kt
import co.touchlab.kermit.Logger

object Counts {
    var d = 0
    var e = 0
}

fun box(): String {
    val ex = RuntimeException("test")
    Logger.d(ex) { Counts.d++; "d with throwable" }
    Logger.e(ex) { Counts.e++; "e with throwable" }
    if (Counts.d != 0) return "FAIL: expected d=0 but was ${Counts.d}"
    if (Counts.e != 1) return "FAIL: expected e=1 but was ${Counts.e}"
    return "OK"
}
