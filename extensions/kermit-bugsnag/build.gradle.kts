/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

apply(from = "../../gradle/configure-crash-logger.gradle")
kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    targetHierarchy.default()
    androidTarget {
        publishAllLibraryVariants()
    }

    sourceSets {
        commonMain {
            dependencies {
                api(libs.crashkios.bugsnag)
            }
        }
    }
}

android {
    namespace = "co.touchlab.kermit.bugsnag"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}