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
    buildsrc.convention.`android-library`
    buildsrc.convention.`kotlin-multiplatform`
    buildsrc.convention.`publish-kotlin-multiplatform`
}

val STATELY_VERSION: String by project

kotlin {
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
        implementation(project(":kermit"))
        implementation("co.touchlab:stately-collections:$STATELY_VERSION")
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
    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
