import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    id("com.vanniktech.maven.publish")
}

dependencies {
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")

    testImplementation(kotlin("test-junit"))
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("dev.zacsweers.kctfork:core:0.12.1")
}

buildConfig {
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
}

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}
