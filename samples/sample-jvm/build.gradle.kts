/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
    extra.apply {
        val parentKermit = java.util.Properties().apply { load(java.io.StringReader(File("${projectDir.path}/../../gradle.properties").readText())) }.get("VERSION_NAME") as String
        set("KERMIT_VERSION", parentKermit)
    }
    dependencies {
        fun readParentKotlin():String = java.util.Properties().apply { load(java.io.StringReader(File("${projectDir.path}/../../gradle.properties").readText())) }.get("KOTLIN_VERSION") as String

        classpath("com.android.tools.build:gradle:7.0.2")
        classpath(kotlin("gradle-plugin", readParentKotlin()))
    }
}

plugins {
    kotlin("jvm") version "1.6.20"
    application
}

group = "me.kgalligan"
version = "1.0-SNAPSHOT"

val KERMIT_VERSION: String by project

repositories {
    mavenLocal()
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    google()
}

dependencies {
    implementation("co.touchlab:kermit:${KERMIT_VERSION}")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

tasks.register("ciTest") {
    dependsOn("build")
}
