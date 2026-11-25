import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
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
    generateTestsEnabled = false
}

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}

// The DevKit's functional-test publishing signs every publication, so `build` needs signing to be optional
// locally. Release builds provide a signatory and are still signed.
tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf { signatory != null }
}
