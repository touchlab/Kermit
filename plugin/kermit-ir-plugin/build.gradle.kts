import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("multiplatform")
    //id("com.vanniktech.maven.publish")
    pluginDevKit("compiler-plugin")
}

pluginDevKit {
    componentRegistrar = "co.touchlab.kermit.irplugin.KermitCompilerPluginRegistrar"
    commandLineProcessor = "co.touchlab.kermit.irplugin.KermitCommandLineProcessor"
}

/*
mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}*/
