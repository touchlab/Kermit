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
    kotlin("multiplatform")
}

kotlin {
    js {
        browser()
        nodejs()
    }

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosDeviceArm64()
    watchosX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()

    mingwX64()
    linuxX64()

    androidNativeArm32()
    androidNativeArm64()
    androidNativeX86()
    androidNativeX64()

    val commonMain by sourceSets.getting {
        dependencies {
            api(project(":kermit"))
        }
    }
    val commonTest by sourceSets.getting {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            implementation(project(":kermit-test"))
        }
    }

    val nonKotlinMain by sourceSets.creating {
        dependsOn(commonMain)
    }

    val nonKotlinTest by sourceSets.creating {
        dependsOn(commonTest)
    }

    val jsMain by sourceSets.getting {
        dependsOn(nonKotlinMain)
        dependencies {
        }
    }

    val jsTest by sourceSets.getting {
        dependsOn(nonKotlinTest)
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-js")
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        mainSourceSet.dependsOn(nonKotlinMain)
        testSourceSet.dependsOn(nonKotlinTest)
    }
}

apply(plugin = "com.vanniktech.maven.publish")
