import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.vanniktech.maven.publish.DeploymentValidation

/*
 * Copyright (c) 2026 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    id("com.vanniktech.maven.publish")
}

kotlin {
    compilerOptions {
        // Kermit uses expect/actual classes, which are still Beta.
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

    applyDefaultHierarchyTemplate {
        common {
            group("jsAndWasmJs") {
                withJs()
                withWasmJs()
            }

            group("commonWasm") {
                withWasmJs()
                withWasmWasi()
            }

            group("commonJvm") {
                withCompilations { it.target.name == "android" }
                withJvm()
            }
        }
    }
}

project.afterEvaluate {
    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
}

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}
