package co.touchlab.kermitsample

import co.touchlab.kermit.Kermit

class SampleCommon(private val kermit: Kermit) {
    private var count = 0
    fun onClick() {
        count++
        kermit.i { "Common click count: $count" }
    }
}