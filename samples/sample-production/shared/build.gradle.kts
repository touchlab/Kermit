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

val KERMIT_VERSION: String by project

kotlin {
    version = "0.0.1"
    android()
    ios()
//    iosX64("ios")

    sourceSets {
        commonMain {
            dependencies {
                api("co.touchlab:kermit:${KERMIT_VERSION}")
                implementation("co.touchlab:kermit-crashlytics:${KERMIT_VERSION}")
            }
        }

        commonTest {
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

            }
        }
        val iosTest by sourceSets.getting {
            dependencies {
                implementation("co.touchlab:kermit-crashlytics-test:${KERMIT_VERSION}")
            }
        }

    }
    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        framework {
            export("co.touchlab:kermit:${KERMIT_VERSION}")
            isStatic = true
        }
    }
}

android {
    compileSdk = 29
    defaultConfig {
        minSdk = 26
        targetSdk = 29
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
