package buildsrc.convention

plugins {
    `maven-publish`
    signing
}

description = "Configuration for publishing Kotlin Multiplatform libraries to Sonatype Maven Central"

val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project

val signingEnabled: Provider<Boolean> = provider {
    signingKeyId != null && signingKey != null && signingPassword != null
}

val javadocJar by tasks.registering(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Empty Javadoc Jar (required by Maven Central)"
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        // publish to local dir, for testing
        maven(rootProject.layout.buildDirectory.dir("maven-internal")) {
            name = "LocalProjectDir"
        }

        // TODO Groovy -> Kotlin
//        if (isPrivateBuild()) {
//            maven {
//                name "AWS"
//                url AWS_REPO_URL
//                        credentials(AwsCredentials.class) {
//                            accessKey(getAwsAccessKey())
//                            secretKey(getAwsSecretKey())
//                        }
//            }
//        } else {
//            maven {
//                url isReleaseBuild() ? getReleaseRepositoryUrl() : getSnapshotRepositoryUrl()
//                credentials {
//                    username getRepositoryUsername()
//                    password getRepositoryPassword()
//                }
//            }
//        }

    }
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar)

        // TODO add POM
    }
}

signing {
    if (signingEnabled.get()) {
        sign(publishing.publications)

        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }
}

val publishWindows by tasks.registering {
    dependsOn(tasks.matching { it.name == "publishMingwX64PublicationToMavenRepository" })
    dependsOn(tasks.matching { it.name == "publishMingwX86PublicationToMavenRepository" })
}

val publishLinux by tasks.registering {
    dependsOn(tasks.matching { it.name == "publishLinuxMips32PublicationToMavenRepository" })
}
