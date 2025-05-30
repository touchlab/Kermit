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
include(":kermit-core")
include(":kermit")
include(":kermit-io")
include(":kermit-simple")
include(":kermit-test")

include(":kermit-crashlytics")
include(":kermit-bugsnag")
include(":kermit-koin")

project(":kermit-crashlytics").projectDir = File("extensions/kermit-crashlytics")
project(":kermit-bugsnag").projectDir = File("extensions/kermit-bugsnag")
project(":kermit-koin").projectDir = File("extensions/kermit-koin")

// include(":kermit-gradle-plugin")
// include(":kermit-ir-plugin")
// include(":kermit-ir-plugin-native")
//
// project(":kermit-gradle-plugin").projectDir = File("plugin/kermit-gradle-plugin")
// project(":kermit-ir-plugin").projectDir = File("plugin/kermit-ir-plugin")
// project(":kermit-ir-plugin-native").projectDir = File("plugin/kermit-ir-plugin-native")

pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
