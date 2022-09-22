/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.bugsnag.android.gradle")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "co.touchlab.kermitsamplebugsnag"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "0.0.1"
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release")  {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.bundles.android)

    // Crash reporters
    implementation(libs.bugsnag.android)
}
