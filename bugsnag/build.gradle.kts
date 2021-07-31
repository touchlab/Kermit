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
//    id("com.android.library") version "4.1.2"
    kotlin("multiplatform")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

val ideaActive = System.getProperty("idea.active") == "true"

repositories {
    google()
    mavenCentral()
}

fun configInterop(target: org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget) {
    val main by target.compilations.getting
    val bugsnag by main.cinterops.creating {
        includeDirs("$projectDir/src/include")
    }
}

kotlin {
    val knTargets = if(ideaActive){
        listOf(macosX64("darwin"))
    }else{
        listOf(
            macosX64(),
            iosX64(),
            iosArm64(),
            iosArm32(),
            tvosArm64(),
            tvosX64()
        )
    }

    knTargets
        .forEach { target ->
            configInterop(target)
        }

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation(project(":kermit"))
                implementation(project(":crashlogging"))
            }
        }
        commonTest {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        if(!ideaActive) {
            val darwinMain = sourceSets.maybeCreate("darwinMain").apply {
                dependsOn(sourceSets.maybeCreate("commonMain"))
            }
            val darwinTest = sourceSets.maybeCreate("darwinTest").apply {
                dependsOn(sourceSets.maybeCreate("commonTest"))
            }

            knTargets.forEach { target ->
                target.compilations.getByName("main").source(darwinMain)
                target.compilations.getByName("test").source(darwinTest)
            }
        }
    }
}

//kotlin {
//    android {
//        publishAllLibraryVariants()
//    }

//    val darwinTargets = listOf(
//        "macosX64",
//        "iosArm32",
//        "iosArm64",
//        "iosX64",
//        "tvosArm64",
//        "tvosX64"
//    )
//
//    if (ideaActive) {
//        val target = macosX64("darwin")
//        configInterop(target)
//    } else {
//        presets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset<*>>().forEach { preset ->
//            if(darwinTargets.contains(preset.name)){
//                val t = targetFromPreset(preset)
//                configInterop(t as org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget)
//            }
//        }
//    }
//
//    sourceSets {
//        commonMain {
//            dependencies {
//                implementation(kotlin("stdlib-common"))
//            }
//        }
//
//        commonTest {
//            dependencies {
//                implementation(kotlin("test-common"))
//                implementation(kotlin("test-annotations-common"))
//            }
//        }
//
//        /*val androidMain by sourceSets.getting {
//            dependencies {
//                implementation(kotlin("stdlib"))
//            }
//        }
//        val androidTest by sourceSets.getting {
//            dependencies {
//                implementation(kotlin("test"))
//                implementation(kotlin("test-junit"))
//                implementation("androidx.test:runner:1.2.0")
//                implementation("org.robolectric:robolectric:4.3.1")
//            }
//        }*/
////        val nativeMain = sourceSets.maybeCreate("nativeMain").apply {
////            dependsOn(commonMain.get())
////        }
//        val darwinMain = sourceSets.maybeCreate("darwinMain").apply {
//            dependsOn(commonMain.get())
//        }
//
//        if (!ideaActive) {
//            configure(
//                darwinTargets.map { targets.findByName(it) }.filterNotNull()
//            ) {
//                compilations.findByName("main")?.source(darwinMain)
//
//                sourceSets.findByName("darwinTest")?.let {
//                    compilations.findByName("test")?.source(it)
//                }
//            }
//        }
//    }
//}

//android {
//    compileSdkVersion(29)
//    defaultConfig {
//        minSdkVersion(15)
//    }
//
//    val main by sourceSets.getting {
//        manifest.srcFile("src/androidMain/AndroidManifest.xml")
//    }
//}

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
