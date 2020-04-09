package co.touchlab.kermitsample

import co.touchlab.kermit.Kermit
import co.touchlab.kermit.LogcatLogger

fun logAndroid(){
    val kermit = Kermit(LogcatLogger())
    kermit.i { "Log Android Shared" }
}