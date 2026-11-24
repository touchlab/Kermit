import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("jvm")
    //id("com.vanniktech.maven.publish")
    pluginDevKit("gradle-plugin")
}

dependencies {
    testImplementation(kotlin("gradle-plugin-api"))
    testImplementation(kotlin("test-junit"))
}

pluginDevKit {
    compilerPlugin(project(":kermit-ir-plugin"))
}

gradlePlugin {
    plugins {
        create("kermitPlugin") {
            id = rootProject.extra["kotlin_plugin_id"] as String
            displayName = "Kermit Log Gradle Plugin"
            description = "Kermit Log Gradle Plugin"
            implementationClass = "co.touchlab.kermit.gradle.KermitGradlePlugin"
        }
    }
}

/*
mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}*/
