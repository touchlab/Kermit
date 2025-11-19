@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

/*
 * Copyright (c) 2024 Touchlab
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
val kotlinVersion = libs.versions.kotlin.get()

// TODO: Kermit is running Kotlin 2.2.0, the Kotlin/Wasm standard library wants to use 2.2.20. So we need to manually set the wasm version
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-stdlib-wasm-js:${kotlinVersion}")
    }
}

kotlin {
    wasmJs {
        binaries.executable()
        browser()
    }
    sourceSets {
        wasmJsMain.dependencies {
            implementation(project(":shared"))
            implementation("org.jetbrains.kotlinx:kotlinx-browser-wasm-js:0.5.0")
            implementation("co.touchlab:kermit-simple:${KERMIT_VERSION}")
        }
    }
}

