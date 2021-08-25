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
    id("com.android.library")
    kotlin("multiplatform")
}

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

kotlin {
    android {
        publishAllLibraryVariants()
    }
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }

    macosX64()
    iosX64()
    iosArm64()
    iosArm32()
    watchosArm32()
    watchosArm64()
    watchosX86()
    watchosX64()
    tvosArm64()
    tvosX64()
    mingwX64()
    mingwX86()
    linuxX64()
    linuxArm32Hfp()
    linuxMips32()

    val commonMain by sourceSets.getting
    val commonTest by sourceSets.getting

    val jvmMain by sourceSets.getting
    val jvmTest by sourceSets.getting

    val androidMain by sourceSets.getting
    val androidTest by sourceSets.getting

    val jsMain by sourceSets.getting
    val jsTest by sourceSets.getting

    val darwinMain by sourceSets.creating

    darwinMain.dependsOn(commonMain)

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        val useDarwin = konanTarget.family.isAppleFamily

        mainSourceSet.dependsOn(
            if (useDarwin) {
                darwinMain
            } else {
                commonMain
            }
        )
        testSourceSet.dependsOn(commonTest)
    }

    commonMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
    }

    commonTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-common")
        implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
    }

    androidMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }

    androidTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.jetbrains.kotlin:kotlin-test-junit")
        implementation("androidx.test:runner:1.2.0")
        implementation("org.robolectric:robolectric:4.3.1")
    }

    jvmMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib")
    }

    jvmTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.jetbrains.kotlin:kotlin-test-junit")
    }

    jsMain.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
    }

    jsTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-js")
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

tasks.register("publishWindows") {
    if (tasks.findByName("publish") != null && tasks.findByName("publishMingwX64PublicationToMavenRepository") != null) {
        dependsOn("publishMingwX64PublicationToMavenRepository",
            "publishMingwX86PublicationToMavenRepository")
    }
}

tasks.register("publishLinux") {
    if (tasks.findByName("publish") != null && tasks.findByName("publishLinuxMips32PublicationToMavenRepository") != null) {
        dependsOn("publishLinuxMips32PublicationToMavenRepository")
    }
}

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
