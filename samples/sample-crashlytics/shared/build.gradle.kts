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

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

val KERMIT_VERSION: String by project

kotlin {
    version = "0.1.2"
    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }
    android()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("co.touchlab:kermit:${KERMIT_VERSION}")
                implementation("co.touchlab:kermit-crashlytics:${KERMIT_VERSION}")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by sourceSets.getting {}
        val iosMain by sourceSets.getting {}
    }

    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        ios.deploymentTarget = "13.5"
        framework {
            export("co.touchlab:kermit:${KERMIT_VERSION}")
            isStatic = false
        }
    }

    crashlyticsLinkerConfig()
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