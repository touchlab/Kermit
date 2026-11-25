import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
    pluginDevKit("compiler-plugin")
}

pluginDevKit {
    componentRegistrar = "co.touchlab.kermit.irplugin.KermitCompilerPluginRegistrar"
    commandLineProcessor = "co.touchlab.kermit.irplugin.KermitCommandLineProcessor"
}

// The DevKit compiles the test generator against the newest known Kotlin libraries, which are newer than the
// Kotlin version this build uses.
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>()
    .matching { it.name.startsWith("compileGenerateTests") }
    .configureEach { compilerOptions.freeCompilerArgs.add("-Xskip-metadata-version-check") }

// ktlint reads the generated test sources, so it has to run after the generator.
tasks.matching { it.name.startsWith("runKtlint") }
    .configureEach { mustRunAfter(tasks.matching { it.name.startsWith("generateTests") }) }

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}

// The DevKit's functional-test publishing signs every publication, so `build` needs signing to be optional
// locally. Release builds provide a signatory and are still signed.
tasks.withType<org.gradle.plugins.signing.Sign>().configureEach {
    onlyIf { signatory != null }
}
