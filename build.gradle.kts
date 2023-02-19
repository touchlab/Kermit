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
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.23.1")
    }
}

plugins {
    kotlin("multiplatform") apply false
    id("com.android.library") version "7.3.1" apply false
    id("com.github.gmazzo.buildconfig") version "2.1.0" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.12.1"
    id("org.jetbrains.dokka") version "1.7.20" apply false
}
apiValidation {
    nonPublicMarkers.add("co.touchlab.kermit.ExperimentalKermitApi")
//    ignoredProjects.addAll(listOf("kermit-gradle-plugin", "kermit-ir-plugin", "kermit-ir-plugin-native"))
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
    apply(plugin = "org.jetbrains.dokka")
    repositories {
        mavenLocal()
        mavenCentral()
        google()
    }
}