/*
 * Copyright (c) 2024 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

android {
    namespace = "co.touchlab.kermitsample"
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

val KERMIT_VERSION: String by project

version = "0.0.1"

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    js {
        nodejs()
    }
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        nodejs()
    }

    sourceSets {
        commonMain.dependencies {
            implementation("co.touchlab:kermit:2.0.7")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))

            implementation("co.touchlab:kermit-test:2.0.7")
        }
        val mobileMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation("co.touchlab:kermit-io:${KERMIT_VERSION}")
            }
        }
        androidMain {
            dependsOn(mobileMain)
        }
        iosMain {
            dependsOn(mobileMain)
            iosX64Main.get().dependsOn(this)
            iosArm64Main.get().dependsOn(this)
            iosSimulatorArm64Main.get().dependsOn(this)
            dependencies {
                // Only if you want to talk to Kermit from Swift
                api("co.touchlab:kermit-simple:${KERMIT_VERSION}")
            }
        }
    }
    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        framework {
            isStatic = true

            // Only if you want to talk to Kermit from Swift
            export("co.touchlab:kermit-simple:${KERMIT_VERSION}")
        }
    }
}
