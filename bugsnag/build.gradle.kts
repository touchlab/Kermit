/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

plugins {
    kotlin("multiplatform")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

fun org.gradle.api.Project.configBugReporter(){
    //Write generated Kotlin to disk. This tends to mess up
    //the compiler, but quite useful to see the output during dev
    val printCInteropKotlin = false

    fun configInterop(target: org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
        val main by target.compilations.getting
        val bugsnag by main.cinterops.creating {
            includeDirs("$projectDir/src/include")
            if (printCInteropKotlin) {
                extraOpts = listOf("-mode", "sourcecode")
            }
        }
    }

    kotlin {
        listOf(
            macosX64(),
            macosArm64(),
            iosX64(),
            iosArm64(),
            iosArm32(),
            iosSimulatorArm64(),
            tvosArm64(),
            tvosSimulatorArm64(),
            tvosX64(),
            watchosArm32(),
            watchosArm64(),
            watchosSimulatorArm64(),
            watchosX86(),
            watchosX64()
        ).apply { forEach { target -> configInterop(target) } }

        val commonMain by sourceSets.getting
        val commonTest by sourceSets.getting

        val darwinMain by sourceSets.creating

        darwinMain.dependsOn(commonMain)

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
            val mainSourceSet = compilations.getByName("main").defaultSourceSet
            val testSourceSet = compilations.getByName("test").defaultSourceSet

            mainSourceSet.dependsOn(darwinMain)
            testSourceSet.dependsOn(commonTest)
        }

        commonMain.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            implementation(project(":kermit"))
            implementation(project(":crashlogging"))
        }

        commonTest.dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
        }
    }
}

configBugReporter()

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
