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
rootProject.name = "kermit-root"

include(
    ":kermit",
    ":kermit-crashlytics",
    ":kermit-crashlytics-test",
    ":kermit-bugsnag",
    ":kermit-bugsnag-test",
    ":kermit-test",
    ":kermit-koin",
    ":kermit-sentry",
    ":kermit-sentry-test",

    ":kermit-gradle-plugin",
    ":kermit-ir-plugin",
    ":kermit-ir-plugin-native",

    ":kermit-gradle-plugin",
    ":kermit-ir-plugin",
    ":kermit-ir-plugin-native",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "./buildSrc/repositories.settings.gradle.kts")

//pluginManagement {
//    resolutionStrategy {
//        eachPlugin {
//            when (requested.id.id) {
//                "com.android.library" -> useModule("com.android.tools.build:gradle:${requested.version}")
//            }
//        }
//    }
//    repositories {
//        mavenLocal()
//        google()
//        gradlePluginPortal()
//        mavenCentral()
//    }
//    val KOTLIN_VERSION: String by settings
//    plugins {
//        kotlin("multiplatform") version KOTLIN_VERSION
//    }
//}

//plugins {
//    id("com.github.burrunan.s3-build-cache") version "1.2"
//}
//
//buildCache {
//    local {
//        isEnabled = false
//    }
//
//    val env = System.getenv()
//    remote<com.github.burrunan.s3cache.AwsS3BuildCache> {
//        region = env.getOrDefault("S3_BUILD_CACHE_AWS_REGION", "")
//        bucket = env.getOrDefault("S3_BUILD_CACHE_BUCKET_NAME", "")
//        awsAccessKeyId = env.getOrDefault("S3_BUILD_CACHE_ACCESS_KEY_ID", "")
//        awsSecretKey = env.getOrDefault("S3_BUILD_CACHE_SECRET_KEY", "")
//        prefix = "${rootProject.name}/"
//        isPush = env.containsKey("CI")
//    }
//}
