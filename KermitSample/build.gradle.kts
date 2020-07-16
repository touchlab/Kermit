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
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath(kotlin("gradle-plugin", "1.3.72"))
        classpath("co.touchlab:kotlinnativecocoapods:0.9")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:9.2.1")
    }
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }
    apply(plugin= "org.jlleitschuh.gradle.ktlint")

    ktlint {
        version.set("0.37.2")
        enableExperimentalRules.set(true)
        verbose.set(true)
        filter {
            exclude { it.file.path.contains("build/") }
        }
    }

    afterEvaluate {
        tasks.named("check").configure {
            dependsOn(tasks.getByName("ktlintCheck"))
        }
    }
}