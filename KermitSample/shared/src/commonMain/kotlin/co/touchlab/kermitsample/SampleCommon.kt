package co.touchlab.kermitsample

import co.touchlab.kermit.Kermit

class SampleCommon(private val kermit: Kermit) {
    private var count = 0
    fun onClick() {
        count++
        kermit.i(throwable = RuntimeException("Sample Throwable")) { "Common click count: $count" }
    }
}