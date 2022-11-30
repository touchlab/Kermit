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
    android {
        publishAllLibraryVariants()
    }
    jvm()
    js(BOTH) {
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

    val commonJvmMain by sourceSets.creating {
        dependsOn(commonMain)
    }
    val commonJvmTest by sourceSets.creating {
        dependsOn(commonTest)
        dependsOn(commonJvmMain)
    }

    val jvmMain by sourceSets.getting {
        dependsOn(commonJvmMain)
    }
    val jvmTest by sourceSets.getting {
        dependsOn(jvmMain)
        dependsOn(commonJvmTest)
    }

    val androidMain by sourceSets.getting {
        dependsOn(commonJvmMain)
    }
    val androidTest by sourceSets.getting {
        dependsOn(androidMain)
        dependsOn(commonJvmTest)
    }

    val jsMain by sourceSets.getting
    val jsTest by sourceSets.getting

    val nativeMain by sourceSets.creating
    nativeMain.dependsOn(commonMain)

    val darwinMain by sourceSets.creating {
        dependsOn(nativeMain)
    }

    val darwinTest by sourceSets.creating {
        dependsOn(commonTest)
    }

    val linuxMain by sourceSets.creating {
        dependsOn(nativeMain)
    }

    val mingwMain by sourceSets.creating {
        dependsOn(nativeMain)
    }

    val androidNativeMain by sourceSets.creating {
        dependsOn(nativeMain)
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        mainSourceSet.dependsOn(
            when {
                konanTarget.family.isAppleFamily -> darwinMain
                konanTarget.family == org.jetbrains.kotlin.konan.target.Family.LINUX -> linuxMain
                konanTarget.family == org.jetbrains.kotlin.konan.target.Family.MINGW -> mingwMain
                konanTarget.family == org.jetbrains.kotlin.konan.target.Family.ANDROID -> androidNativeMain
                else -> nativeMain
            }
        )

        testSourceSet.dependsOn(
            if (konanTarget.family.isAppleFamily) {
                darwinTest
            } else {
                commonTest
            }
        )
    }

    commonTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test-common")
        implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
        implementation(libs.stately.collections)
        implementation(libs.testhelp)
    }

    androidTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.jetbrains.kotlin:kotlin-test-junit")
        implementation("androidx.test:runner:1.4.0")
        implementation("org.robolectric:robolectric:4.5.1")
    }

    commonJvmTest.dependencies {
        implementation("org.jetbrains.kotlin:kotlin-test")
        implementation("org.jetbrains.kotlin:kotlin-test-junit")
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
    compileSdk = 30
    defaultConfig {
        minSdk = 16
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}

tasks.register("publishWindows") {
    if (tasks.findByName("publish") != null &&
        tasks.findByName("publishMingwX64PublicationToMavenRepository") != null) {
        dependsOn(
            "publishMingwX64PublicationToMavenRepository",
            "publishMingwX86PublicationToMavenRepository"
        )
    }
}

tasks.register("publishLinux") {
    if (tasks.findByName("publish") != null &&
        tasks.findByName("publishLinuxMips32PublicationToMavenRepository") != null) {
        dependsOn("publishLinuxMips32PublicationToMavenRepository")
    }
}

apply(plugin = "com.vanniktech.maven.publish")
