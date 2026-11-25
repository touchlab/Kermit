// STRIP_BELOW: All
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
    var v = 0
    var d = 0
    var i = 0
    var w = 0
    var e = 0
    var a = 0
}

fun box(): String {
    Logger.v { Counts.v++; "v" }
    Logger.d { Counts.d++; "d" }
    Logger.i { Counts.i++; "i" }
    Logger.w { Counts.w++; "w" }
    Logger.e { Counts.e++; "e" }
    Logger.a { Counts.a++; "a" }
    if (Counts.v != 0) return "FAIL: expected v=0 but was ${Counts.v}"
    if (Counts.d != 0) return "FAIL: expected d=0 but was ${Counts.d}"
    if (Counts.i != 0) return "FAIL: expected i=0 but was ${Counts.i}"
    if (Counts.w != 0) return "FAIL: expected w=0 but was ${Counts.w}"
    if (Counts.e != 0) return "FAIL: expected e=0 but was ${Counts.e}"
    if (Counts.a != 0) return "FAIL: expected a=0 but was ${Counts.a}"
    return "OK"
}
