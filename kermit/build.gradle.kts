/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

plugins {
    id("com.android.library") version "3.6.1"
    kotlin("multiplatform") version "1.4.0"
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

val ideaActive = System.getProperty("idea.active") == "true"

repositories {
    google()
    mavenCentral()
    jcenter()
}

kotlin {
    android {
        publishAllLibraryVariants()
    }
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }

    val darwinTargets = listOf(
        "macosX64",
        "iosArm32",
        "iosArm64",
        "iosX64",
        "watchosArm32",
        "watchosArm64",
        "watchosX86",
        "tvosArm64",
        "tvosX64"
    )

    val nonDarwinTargets = mutableListOf<String>()

    if (ideaActive) {
        macosX64("darwin")
    } else {
        presets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>().forEach { preset ->
            if(!darwinTargets.contains(preset.name)){
                nonDarwinTargets.add(preset.name)
            }
            targetFromPreset(preset)
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val androidTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("androidx.test:runner:1.2.0")
                implementation("org.robolectric:robolectric:4.3.1")
            }
        }
        val nativeMain = sourceSets.maybeCreate("nativeMain").apply {
            dependsOn(commonMain.get())
        }
        val darwinMain = sourceSets.maybeCreate("darwinMain").apply {
            dependsOn(nativeMain)
        }
        val jsMain = sourceSets.maybeCreate("jsMain").apply {
            dependsOn(commonMain.get())
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest = sourceSets.maybeCreate("jsTest").apply {
            dependsOn(commonTest.get())
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        if (!ideaActive) {
            configure(
                darwinTargets.map { targets.findByName(it) }.filterNotNull()
            ) {
                compilations.findByName("main")?.source(darwinMain)

                sourceSets.findByName("darwinTest")?.let {
                    compilations.findByName("test")?.source(it)
                }
            }

            configure(
                nonDarwinTargets.map { targets.findByName(it) }.filterNotNull()
            ) {
                compilations.findByName("main")?.source(nativeMain)

                sourceSets.findByName("nativeTest")?.let {
                    compilations.findByName("test")?.source(it)
                }
            }
        }
        val jvmMain = sourceSets.maybeCreate("jvmMain").apply {
            dependsOn(commonMain.get())
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val jvmTest = sourceSets.maybeCreate("jvmTest").apply {
            dependsOn(commonTest.get())
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
