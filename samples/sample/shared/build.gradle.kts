/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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
    version = "0.0.1"
    android()
    ios()
    js {
        browser()
    }

    sourceSets {
        commonMain {
            dependencies {
                api("co.touchlab:kermit:${KERMIT_VERSION}")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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
        }
        val jsMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        framework {
            export("co.touchlab:kermit:${KERMIT_VERSION}")
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
