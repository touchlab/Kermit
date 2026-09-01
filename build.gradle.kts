import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

buildscript {
    extra["kotlin_plugin_id"] = "co.touchlab.kermit"
}

plugins {
    alias(libs.plugins.buildConfig) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.binaryCompatability)
    alias(libs.plugins.dokka) apply false
    alias(libs.plugins.touchlab.docusaurus.template)
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ktlint.gradle) apply false
}

apiValidation {
    nonPublicMarkers.add("co.touchlab.kermit.ExperimentalKermitApi")
//    ignoredProjects.addAll(listOf("kermit-gradle-plugin", "kermit-ir-plugin", "kermit-ir-plugin-native"))
}

val GROUP: String by project
val VERSION_NAME: String by project

allprojects {
    group = GROUP
    version = VERSION_NAME
}

allprojects {
    apply(plugin = "org.jetbrains.dokka")

    repositories {
        mavenCentral()
        google()
    }
    tasks.getByName("dokkaHtml").dependsOn(":kermit:transformIosMainCInteropDependenciesMetadataForIde")
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("1.4.0")
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }

    afterEvaluate {
        tasks.named("check") {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}

/*
 * Workaround for https://github.com/Kotlin/binary-compatibility-validator/issues/315
 *
 * BCV only registers its Android API tasks for a compilation named "release", which is what
 * `com.android.library` plus `androidTarget()` used to produce. The AGP 9 KMP plugin
 * (`com.android.kotlin.multiplatform.library`) names its compilations `main`, `hostTest` and
 * `deviceTest`, so BCV matches nothing, registers no tasks, and the Android dumps silently stop
 * being validated or updated while the build stays green.
 *
 * BCV is in maintenance mode and the fix landed in the Kotlin Gradle plugin's built-in ABI
 * validation instead (KT-85950, Kotlin 2.4.20). Until we migrate to that, register the missing
 * `androidApi*` tasks by hand against the `main` compilation, mirroring what BCV does for
 * `release`.
 */
subprojects {
    afterEvaluate {
        val kotlinExtension = extensions.findByType<KotlinMultiplatformExtension>() ?: return@afterEvaluate
        val androidTarget = kotlinExtension.targets.findByName("android") ?: return@afterEvaluate
        val androidMainCompilation = androidTarget.compilations.findByName("main") ?: return@afterEvaluate
        if (tasks.names.contains("androidApiBuild")) return@afterEvaluate

        // BCV only uses an `api/<target>/` subdirectory when a module has more than one JVM target.
        val jvmLikeTargetCount = kotlinExtension.targets.count {
            it.platformType == KotlinPlatformType.jvm || it.platformType == KotlinPlatformType.androidJvm
        }
        val apiDirName = if (jvmLikeTargetCount == 1) "api" else "api/android"
        val dumpFileName = "$name.api"
        val referenceApiFile = layout.projectDirectory.file("$apiDirName/$dumpFileName")

        val androidApiBuild = tasks.register<KotlinApiBuildTask>("androidApiBuild") {
            description = "Builds Kotlin API for 'main' compilations of ${project.name}. " +
                "Complementary task and shouldn't be called manually"
            inputClassesDirs.from(androidMainCompilation.output.classesDirs)
            outputApiFile.set(layout.buildDirectory.file("$apiDirName/$dumpFileName"))
            runtimeClasspath.from(configurations.named("bcv-rt-jvm-cp-resolver"))
        }

        val androidApiCheck = tasks.register<KotlinApiCompareTask>("androidApiCheck") {
            group = "verification"
            description = "Checks signatures of public API against the golden value in API folder for ${project.name}"
            projectApiFile.set(referenceApiFile)
            generatedApiFile.set(androidApiBuild.flatMap { it.outputApiFile })
        }

        val androidApiDump = tasks.register("androidApiDump") {
            group = "other"
            description = "Syncs the API file for ${project.name}"
            val generatedApiFile = androidApiBuild.flatMap { it.outputApiFile }
            inputs.file(generatedApiFile)
            outputs.file(referenceApiFile)
            doLast {
                val target = referenceApiFile.asFile
                target.parentFile.mkdirs()
                generatedApiFile.get().asFile.copyTo(target, overwrite = true)
            }
        }

        tasks.named("apiCheck") { dependsOn(androidApiCheck) }
        tasks.named("apiDump") { dependsOn(androidApiDump) }
    }
}
