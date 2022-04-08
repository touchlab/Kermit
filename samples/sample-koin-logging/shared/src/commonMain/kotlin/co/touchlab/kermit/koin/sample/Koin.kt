package co.touchlab.kermit.koin.sample

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import co.touchlab.kermit.koin.kermitLoggerModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module

expect val platformModule: Module
expect val platformViewModelsModule: Module

fun initializeKoin(vararg modules: Module): KoinApplication {
    val combinedModulesList = mutableListOf<Module>().apply {
        add(kermitLoggerModule(Logger))
        add(platformModule)
        add(coreModule)
        add(repositoriesModule)
        add(useCaseModule)
        add(platformViewModelsModule)
        addAll(modules)
    }

    val koinApplication = startKoin {
        modules(combinedModulesList)

        logger(
            KermitKoinLogger(Logger.withTag("koin"))
        )
    }

    return koinApplication
}