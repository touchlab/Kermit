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

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    jvm()
    js {
        browser()
        nodejs()
    }

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosArm32()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()
    watchosX86()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    mingwX64()
    mingwX86()
    linuxX64()
    linuxArm32Hfp()
    linuxMips32()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    val commonMain by sourceSets.getting
    val commonTest by sourceSets.getting

    val jsMain by sourceSets.getting
    val jsTest by sourceSets.getting

    val jvmMain by sourceSets.getting {
        dependsOn(commonMain)
    }

    val androidMain by sourceSets.getting {
        dependsOn(commonMain)
    }

    commonMain.dependencies {
        implementation(kotlin("test-common"))
        api(project(":kermit-core"))
        implementation(libs.stately.collections)
    }

    jsMain.dependencies {
        implementation(kotlin("stdlib-js"))
        implementation(kotlin("test-js"))
    }

    jvmMain.dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
    }

    androidMain.dependencies {
        implementation(kotlin("test"))
        implementation(kotlin("test-junit"))
    }
}

android {
    namespace = "co.touchlab.kermit.test"
    compileSdk = 30
    defaultConfig {
        minSdk = 16
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

apply(plugin = "com.vanniktech.maven.publish")
