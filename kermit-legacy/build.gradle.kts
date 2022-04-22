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

val STATELY_VERSION: String by project
val TESTHELP_VERSION: String by project

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

    val commonMain by sourceSets.getting {
        dependencies {
            api(project(":kermit-core"))
        }
    }
    val commonTest by sourceSets.getting {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-common")
            implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            implementation("co.touchlab:stately-collections:$STATELY_VERSION")
            implementation("co.touchlab:testhelp:$TESTHELP_VERSION")
            implementation(project(":kermit-test"))
        }
    }

    val nonKotlinMain by sourceSets.creating {
        dependsOn(commonMain)
    }

    val nativeMain by sourceSets.creating {
        dependsOn(nonKotlinMain)
    }

    val darwinMain by sourceSets.creating {
        dependsOn(nativeMain)
    }

    val jsMain by sourceSets.getting {
        dependsOn(nonKotlinMain)
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-stdlib-js")
        }
    }

    val jsTest by sourceSets.getting {
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test-js")
        }
    }

    val commonJvmMain by sourceSets.creating {
        dependsOn(commonMain)
        dependencies {
            implementation("org.jetbrains.kotlin:kotlin-test")
            implementation("org.jetbrains.kotlin:kotlin-test-junit")
        }
    }

    val commonJvmTest by sourceSets.creating {
        dependsOn(commonTest)
        dependsOn(commonJvmMain)
    }

    val jvmMain by sourceSets.getting {
        dependsOn(commonJvmMain)
    }

    val androidMain by sourceSets.getting {
        dependsOn(commonJvmMain)
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget>().all {
        val mainSourceSet = compilations.getByName("main").defaultSourceSet
        val testSourceSet = compilations.getByName("test").defaultSourceSet

        mainSourceSet.dependsOn(
            when {
                konanTarget.family.isAppleFamily -> darwinMain
                else -> nativeMain
            }
        )

        testSourceSet.dependsOn(commonTest)
    }
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(15)
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

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
