/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("js")
    id("org.jlleitschuh.gradle.ktlint")
}

kotlin.target.browser {
    runTask {
        outputFileName = "app-browser.js"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("stdlib-js"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.6.12")
}

tasks.withType<KotlinJsCompile> {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.Experimental"
}

ktlint {
    version.set("0.37.2")
    enableExperimentalRules.set(true)
    verbose.set(true)
    filter {
        exclude { it.file.path.contains("build/") }
    }
}

afterEvaluate {
    tasks.named("check").configure {
        dependsOn(tasks.getByName("ktlintCheck"))
    }
}
