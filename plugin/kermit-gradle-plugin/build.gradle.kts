plugins {
  id("java-gradle-plugin")
  kotlin("jvm")
  id("com.github.gmazzo.buildconfig")
  id("com.vanniktech.maven.publish")
}

dependencies {
  implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
  val project = project(":kermit-ir-plugin")
  packageName("co.touchlab.kermit.gradle")
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
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
