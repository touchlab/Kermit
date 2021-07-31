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

    sourceSets {
        commonMain {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common")
                implementation(project(":kermit"))
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

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")
