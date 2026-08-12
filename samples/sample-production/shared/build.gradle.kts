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
    alias(libs.plugins.android.library)
    kotlin("multiplatform")
    kotlin("native.cocoapods")
}

val KERMIT_VERSION: String by project

version = "0.0.1"

kotlin {
    android {
        namespace = "co.touchlab.kermitsample.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            api("co.touchlab:kermit:${KERMIT_VERSION}")
            implementation("co.touchlab:kermit-bugsnag:${KERMIT_VERSION}")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("co.touchlab:kermit-test:${KERMIT_VERSION}")
        }

        iosMain.dependencies {
            // Only if you want to talk to Kermit from Swift
            api("co.touchlab:kermit-simple:${KERMIT_VERSION}")
        }
    }

    cocoapods {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
        framework {
            // Only if you want to talk to Kermit from Swift
            export("co.touchlab:kermit-simple:${KERMIT_VERSION}")
            isStatic = true
        }
    }
}
