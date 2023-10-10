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

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default()
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasm {
        browser()
        nodejs()
        d8()
        binaries.executable()
    }

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    mingwX64()
    linuxX64()
    linuxArm64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.stately.collections)
                implementation(libs.testhelp)
                implementation(project(":kermit-test"))
            }
        }

        val commonJvmMain by creating {
            dependsOn(commonMain)
        }
        val commonJvmTest by creating {
            dependsOn(commonTest)
            dependsOn(commonJvmMain)
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmMain by getting {
            dependsOn(commonJvmMain)
        }
        val jvmTest by getting {
            dependsOn(commonJvmTest)
            dependsOn(jvmMain)
        }

        val androidMain by getting {
            dependsOn(commonJvmMain)
        }
        val androidUnitTest by getting {
            dependsOn(androidMain)
            // dependsOn(commonJvmTest)
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.roboelectric)
            }
        }

        val nativeMain by getting
        val nativeTest by getting

        val darwinMain by creating {
            dependsOn(nativeMain)
        }

        val darwinTest by creating {
            dependsOn(nativeTest)
        }

        val jsAndWasmMain by creating {
            dependsOn(commonMain)
            getByName("jsMain").dependsOn(this)
            getByName("wasmMain").dependsOn(this)
        }
        val jsAndWasmTest by creating {
            dependsOn(commonTest)
            getByName("jsTest").dependsOn(this)
            getByName("wasmTest").dependsOn(this)
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
            val mainSourceSet = compilations.getByName("main").defaultSourceSet
            val testSourceSet = compilations.getByName("test").defaultSourceSet

            mainSourceSet.dependsOn(
                when {
                    konanTarget.family.isAppleFamily -> darwinMain
                    konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX -> {
                        val linuxMain by getting
                        linuxMain
                    }
                    konanTarget.family == org.jetbrains.kotlin.konan.target.Family.MINGW -> {
                        val mingwMain by getting
                        mingwMain
                    }
                    konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID -> {
                        val androidNativeMain by getting
                        androidNativeMain
                    }
                    else -> nativeMain
                }
            )

            testSourceSet.dependsOn(
                if (konanTarget.family.isAppleFamily) {
                    darwinTest
                } else {
                    commonTest
                }
            )
        }
    }
}

android {
    namespace = "co.touchlab.kermit.core"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

rootProject.the<NodeJsRootExtension>().apply {
    nodeVersion = "20.4.0"
}