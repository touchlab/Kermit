import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("multiplatform")
    //id("com.vanniktech.maven.publish")
    pluginDevKit("compiler-plugin")
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlin.compiler.embeddable)
            implementation(libs.kctfork)
        }
    }
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
