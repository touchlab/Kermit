/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }

    tasks.register("publishMac") {
        if (tasks.findByName("publish") != null) {
            dependsOn("publishKotlinMultiplatformPublicationToMavenRepository",
                "publishMetadataPublicationToMavenRepository",
                "publishJvmPublicationToMavenRepository",
                "publishAndroidDebugPublicationToMavenRepository",
                "publishAndroidReleasePublicationToMavenRepository",
                "publishAndroidNativeArm32PublicationToMavenRepository",
                "publishAndroidNativeArm64PublicationToMavenRepository",
                "publishAndroidNativeX64PublicationToMavenRepository",
                "publishAndroidNativeX86PublicationToMavenRepository",
                "publishIosArm32PublicationToMavenRepository",
                "publishIosArm64PublicationToMavenRepository",
                "publishIosX64PublicationToMavenRepository",
                "publishMacosX64PublicationToMavenRepository",
                "publishJsPublicationToMavenRepository",
                "publishWatchosArm32PublicationToMavenRepository",
                "publishWatchosArm64PublicationToMavenRepository",
                "publishWatchosX86PublicationToMavenRepository",
                "publishTvosArm64PublicationToMavenRepository",
                "publishTvosX64PublicationToMavenRepository",
                "publishLinuxArm32HfpPublicationToMavenRepository",
                "publishLinuxArm64PublicationToMavenRepository",
                "publishLinuxX64PublicationToMavenRepository",
                "publishWasm32PublicationToMavenRepository")
        }
    }

    tasks.register("publishWindows") {
        if (tasks.findByName("publish") != null) {
            dependsOn("publishMingwX64PublicationToMavenRepository",
                "publishMingwX86PublicationToMavenRepository")
        }
    }

    tasks.register("publishLinux") {
        if (tasks.findByName("publish") != null) {
            dependsOn("publishLinuxMips32PublicationToMavenRepository",
                "publishLinuxMipsel32PublicationToMavenRepository")
        }
    }
}
