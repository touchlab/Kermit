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

class CustomLogger : Logger()

object Counts {
    var v = 0
    var d = 0
    var i = 0
}

fun box(): String {
    val custom = CustomLogger()
    custom.v { Counts.v++; "v" }
    custom.d { Counts.d++; "d" }
    custom.i { Counts.i++; "i" }
    if (Counts.v != 0) return "FAIL: expected v=0 but was ${Counts.v}"
    if (Counts.d != 0) return "FAIL: expected d=0 but was ${Counts.d}"
    if (Counts.i != 1) return "FAIL: expected i=1 but was ${Counts.i}"
    return "OK"
}
