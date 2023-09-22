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

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()
    androidTarget {
        publishAllLibraryVariants()
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    jvm()
    js {
        browser()
        nodejs()
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
        val commonMain by getting {
            dependencies {
                api(project(":kermit-core"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.testhelp)
                implementation(project(":kermit-test"))
            }
        }

        val nonKotlinMain by creating {
            dependsOn(commonMain)
        }

        val nonKotlinTest by creating {
            dependsOn(commonTest)
        }

        val nativeMain by getting {
            dependsOn(nonKotlinMain)
        }

        val jsMain by getting {
            dependsOn(nonKotlinMain)
        }

        val jsTest by getting {
            dependsOn(nonKotlinTest)
        }

        val commonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmMain by getting {
            dependsOn(commonJvmMain)
        }

        val androidMain by getting {
            dependsOn(commonJvmMain)
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
            val mainSourceSet = compilations.getByName("main").defaultSourceSet
            val testSourceSet = compilations.getByName("test").defaultSourceSet

            mainSourceSet.dependsOn(nativeMain)
            testSourceSet.dependsOn(nonKotlinTest)
        }
    }
}

android {
    namespace = "co.touchlab.kermit"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
}