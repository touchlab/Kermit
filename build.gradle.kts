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

buildscript {
    extra["kotlin_plugin_id"] = "co.touchlab.kermit"
    val CKLIB_VERSION: String by project
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
        classpath("co.touchlab:cklib-gradle-plugin:${CKLIB_VERSION}")
    }
}

plugins {
    kotlin("multiplatform") apply false
    id("com.android.library") version "4.1.2" apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.11.0"
}
apiValidation {
    nonPublicMarkers.add("co.touchlab.kermit.ExperimentalKermitApi")
    ignoredProjects.addAll(listOf("kermit-gradle-plugin", "kermit-ir-plugin", "kermit-ir-plugin-native"))
}

val GROUP: String by project
val VERSION_NAME: String by project

allprojects {
    group = GROUP
    version = VERSION_NAME

    extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.apply {
        sourceSets.all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}
