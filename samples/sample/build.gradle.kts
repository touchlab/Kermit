/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
buildscript {
    extra.apply {
        val parentKermit = java.util.Properties()
            .apply { load(java.io.StringReader(File("${projectDir.path}/../../gradle.properties").readText())) }
            .get("VERSION_NAME") as String
        set("KERMIT_VERSION", parentKermit)
    }
}
plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
}

version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        google()
        mavenLocal()
    }
}
tasks.register("ciTest") {
    dependsOn("kotlinUpgradeYarnLock", ":app:build", ":shared:build")
}
