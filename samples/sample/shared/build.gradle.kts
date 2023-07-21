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

version = "0.0.1"

kotlin {
    androidTarget()
    ios()
    iosSimulatorArm64()
    js {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("co.touchlab:kermit:${KERMIT_VERSION}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))

                implementation("co.touchlab:kermit-test:${KERMIT_VERSION}")
            }
        }

        val androidMain by sourceSets.getting {
            dependencies {
            }
        }
        val androidUnitTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val iosMain by sourceSets.getting {
            dependsOn(commonMain)
            dependencies {
                // Only if you want to talk to Kermit from Swift
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

        val jsMain by sourceSets.getting {
            dependencies {
            }
        }
        val jsTest by sourceSets.getting {
            dependencies {
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
