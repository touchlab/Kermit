/*
 * Copyright (c) 2026 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package kermit

import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget

fun KotlinMultiplatformExtension.configureTests() {
    configureJSTests()
}

private fun KotlinMultiplatformExtension.configureJSTests() {
    targets.withType<KotlinJsIrTarget>().configureEach {
        whenBrowserConfigured {
            testTask {
                useKarma {
                    useConfigDirectory(project.rootProject.rootDir.resolve("karma.config.d"))
                    useChromeHeadless()
                }
            }
        }
    }
}
