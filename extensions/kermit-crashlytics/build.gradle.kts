/*
 * Copyright (c) 2024 Touchlab
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

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.multiplatform)
    id("kermit-jvm-target")
    id("kermit-publish")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }

    val excludeX64 = project.findProperty("excludeX64Targets") == "true"
    if(!excludeX64){
        macosX64()
        iosX64()
        tvosX64()
        watchosX64()
    }
    macosArm64()
    iosArm64()
    iosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()

    sourceSets {
        commonMain.dependencies {
            api(libs.crashkios.crashlytics)
            implementation(project(":kermit-core"))
        }
    }
}

android {
    namespace = "co.touchlab.kermit.crashlytics"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
