/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

allprojects{
    repositories{
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }

    allprojects {
        tasks.maybeCreate("publishMac").apply {
            if(tasks.findByName("publish")!= null) {
                dependsOn("publishIosArm32PublicationToMavenRepository")
                dependsOn("publishIosArm64PublicationToMavenRepository")
                dependsOn("publishIosX64PublicationToMavenRepository")
                dependsOn("publishMacosX64PublicationToMavenRepository")
                dependsOn("publishTvosArm64PublicationToMavenRepository")
                dependsOn("publishTvosX64PublicationToMavenRepository")
                dependsOn("publishWatchosArm32PublicationToMavenRepository")
                dependsOn("publishWatchosArm64PublicationToMavenRepository")
                dependsOn("publishWatchosX86PublicationToMavenRepository")
                dependsOn("publishJvmPublicationToMavenRepository")
                dependsOn("publishJsPublicationToMavenRepository")
                dependsOn("publishMetadataPublicationToMavenRepository")
                dependsOn("publishKotlinMultiplatformPublicationToMavenRepository")
                dependsOn("publishLinuxX64PublicationToMavenRepository")
                dependsOn("publishLinuxArm32HfpPublicationToMavenRepository")
            }
        }

        tasks.maybeCreate("publishWindows").apply {
            if(tasks.findByName("publish")!= null) {
                dependsOn("publishMingwX64PublicationToMavenRepository")
            }
        }

        tasks.maybeCreate("publishLinux").apply {
            if(tasks.findByName("publish") != null) {
                dependsOn ("publishLinuxMips32PublicationToMavenRepository")
            }
        }
    }
}
