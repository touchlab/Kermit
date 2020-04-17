package co.touchlab.kermitsample

import co.touchlab.crashkios.CrashHandler
import co.touchlab.crashkios.setupCrashHandler

fun crashInit(handler: CrashHandler){
    setupCrashHandler(handler)
}