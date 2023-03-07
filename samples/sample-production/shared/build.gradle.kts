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
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

android {
    namespace = "co.touchlab.kermitsample"
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

kotlin {
    version = "0.0.1"
    android()
    ios()
    // Note: iosSimulatorArm64 target requires that all dependencies have M1 support
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("co.touchlab:kermit:${KERMIT_VERSION}")
                implementation("co.touchlab:kermit-bugsnag:${KERMIT_VERSION}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("co.touchlab:kermit-test:${KERMIT_VERSION}")
            }
        }

        val androidMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val androidTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }

        val iosMain by sourceSets.getting {
            dependencies {
                api("co.touchlab:kermit-simple:${KERMIT_VERSION}")
            }
        }

        val iosTest by sourceSets.getting {
            dependencies {

            }
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
        val iosSimulatorArm64Test by getting {
            dependsOn(iosTest)
        }
    }

    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        framework {
            export("co.touchlab:kermit-simple:${KERMIT_VERSION}")
            isStatic = true
        }
    }
}
