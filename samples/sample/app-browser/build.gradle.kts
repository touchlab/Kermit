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
    kotlin("multiplatform")
}

val KERMIT_VERSION: String by project

kotlin {
    js {
        browser {
            runTask {
                outputFileName = "app-browser.js"
            }
            binaries.executable()
        }
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.9.1")
                implementation("co.touchlab:kermit-simple:${KERMIT_VERSION}")
            }
        }
    }
}
