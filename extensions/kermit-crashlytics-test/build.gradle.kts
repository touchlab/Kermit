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
    id("co.touchlab.cklib")
}

val CRASHKIOS_CORE_VERSION: String by project
val CRASHLYTICS_ANDROID_VERSION: String by project
val KOTLIN_VERSION: String by project

kotlin {

    macosX64()
    macosArm64()
    iosX64()
    iosArm64()
    iosArm32()
    iosSimulatorArm64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosSimulatorArm64()
    watchosX86()
    watchosX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test-common")
                implementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
            }
        }

        val darwinMain by creating
        darwinMain.dependsOn(commonMain)

        val darwinTest by creating
        darwinTest.dependsOn(commonTest)

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
            val main by compilations.getting
            main.defaultSourceSet.dependsOn(darwinMain)
            val test by compilations.getting
            test.defaultSourceSet.dependsOn(darwinTest)
        }
    }
}

cklib {
    config.kotlinVersion = KOTLIN_VERSION
    create("objcsample") {
        language = co.touchlab.cklib.gradle.CompileToBitcode.Language.OBJC
        compilerArgs.addAll(
            listOf(
                "-DKONAN_MI_MALLOC=1", "-DNS_FORMAT_ARGUMENT(A)=", "-D_Nullable_result=_Nullable"
            )
        )
    }
}

apply(from = "../../gradle/gradle-mvn-mpp-push.gradle")
