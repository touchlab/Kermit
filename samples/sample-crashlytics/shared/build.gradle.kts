/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import co.touchlab.faktory.crashlyticsLinkerConfig

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

val KERMIT_VERSION: String by project

version = "0.1.2"

kotlin {
    android()
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        val commonMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("co.touchlab:kermit")
                implementation("co.touchlab:kermit-crashlytics")
            }
        }

        val commonTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by sourceSets.getting

        val iosMain by sourceSets.getting
        val iosTest by sourceSets.getting

        val iosSimulatorArm64Main by sourceSets.getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Test by sourceSets.getting {
            dependsOn(iosTest)
        }
    }

    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        ios.deploymentTarget = "13.5"
        framework {
            export("co.touchlab:kermit")
            isStatic = false
        }
    }

    crashlyticsLinkerConfig()
}
