/*
 * Copyright (c) 2024 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsRootExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    kotlin("multiplatform")
}

kotlin {
    val wasmEnabled = project.findProperty("enableWasm") == "true"
    if (wasmEnabled) {
        @OptIn(ExperimentalWasmDsl::class)
        wasmJs {
            browser {
                commonWebpackConfig {
                    devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static = (static ?: mutableListOf()).apply {
                            add(project.rootDir.path)
                        }
                    }
                }
            }
            nodejs()
            d8()
            binaries.executable()
        }
    }

    @Suppress("OPT_IN_USAGE")
    applyDefaultHierarchyTemplate {
        common {
            group("jsAndWasmJs") {
                withJs()
                if (wasmEnabled) {
                    withWasm()
                }
            }
        }
    }
}

if (project.findProperty("enableWasm") == "true") {
    rootProject.the<NodeJsRootExtension>().apply {
        nodeVersion = "21.0.0-v8-canary202309143a48826a08"
        nodeDownloadBaseUrl = "https://nodejs.org/download/v8-canary"
    }
}