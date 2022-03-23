package co.touchlab.kermit.koin.sample

import org.koin.core.KoinApplication

@Suppress("unused")
class KoinDependencyHelper {
    private val application: KoinApplication

    init {
        configureKermitLogging()
        application = initializeKoin()
    }

    fun sampleViewModel(): SampleViewModel = application.koin.get()
}