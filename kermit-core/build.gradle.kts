/*
 * Copyright (c) 2024 Touchlab
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

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    js {
        browser()
        nodejs()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
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

    @Suppress("OPT_IN_USAGE")
    applyDefaultHierarchyTemplate {
        common {
            group("commonJvm") {
                withAndroidTarget()
                withJvm()
            }
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.stately.collections)
            implementation(libs.testhelp)
            implementation(project(":kermit-test"))
        }

        val jsAndWasmJsMain by creating {
            dependsOn(commonMain.get())
        }
        jsMain {
            dependsOn(jsAndWasmJsMain)
        }
        wasmJsMain {
            dependsOn(jsAndWasmJsMain)
        }

        getByName("commonJvmTest").dependencies {
            implementation(kotlin("test-junit"))
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
            if (konanTarget.family.isAppleFamily) {
                compilations.getByName("main").cinterops.create("os_log")
            }
        }

        getByName("androidUnitTest").dependencies {
            implementation(libs.androidx.runner)
            implementation(libs.roboelectric)
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

val mingTask = project.tasks.findByName(":kermit-core:mingwX64MetadataElements")
val linux64Task = project.tasks.findByName(":kermit-core:linuxX64MetadataElements")
val linuxArmTask = project.tasks.findByName(":kermit-core:linuxArm64MetadataElements")
val android86Task = project.tasks.findByName(":kermit-core:androidNativeX86MetadataElements")
val android64Task = project.tasks.findByName(":kermit-core:androidNativeX64MetadataElements")

mingTask?.dependsOn(tasks.named(":kermit-core:commonizeCInterop"))
linux64Task?.dependsOn(tasks.named(":kermit-core:commonizeCInterop"))
linuxArmTask?.dependsOn(tasks.named(":kermit-core:commonizeCInterop"))
android86Task?.dependsOn(tasks.named(":kermit-core:commonizeCInterop"))
android64Task?.dependsOn(tasks.named(":kermit-core:commonizeCInterop"))