/*
 * Copyright (c) 2023 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
}

kotlin {
    val wasmEnabled = project.findProperty("enableWasm") == "true"
    if (wasmEnabled) {
        @OptIn(ExperimentalWasmDsl::class)
        wasm {
            browser()
            nodejs()
            d8()
            binaries.executable()
        }
    }
    sourceSets {
        val jsAndWasmMain by creating
        val jsAndWasmTest by creating
        if (wasmEnabled) {
            val wasmMain by getting {
                dependsOn(jsAndWasmMain)
            }

            val wasmTest by getting {
                dependsOn(jsAndWasmTest)
            }
        }
    }
}