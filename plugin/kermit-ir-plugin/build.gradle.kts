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
}

// Kermit's tests use kotlin-compile-testing, not the compiler test framework, so there is no test data to generate.
val generateTestsTasks = tasks.matching { it.name.startsWith("generateTests") }
generateTestsTasks.configureEach { enabled = false }
tasks.matching { it.name.startsWith("runKtlint") }.configureEach { mustRunAfter(generateTestsTasks) }

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}

// The DevKit's functional-test publishing signs every publication, so `build` needs signing to be optional
// locally. Release builds provide a signatory and are still signed.
tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf { signatory != null }
}
