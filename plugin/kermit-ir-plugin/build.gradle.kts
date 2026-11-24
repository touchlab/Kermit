import com.vanniktech.maven.publish.DeploymentValidation

plugins {
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
    //id("com.vanniktech.maven.publish")
    kotlin("compiler.plugin.devkit") version "0.0.4-dev-408"
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    testImplementation(kotlin("test-junit"))
    testImplementation(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kctfork)
}

buildConfig {
    packageName(group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
}

mavenPublishing {
    configureBasedOnAppliedPlugins()
    publishToMavenCentral(true, DeploymentValidation.NONE)
}
