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
  val project = project(":kermit-stripper-plugin")
  packageName("co.touchlab.kermit.stripper")
  buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${rootProject.extra["kotlin_plugin_id"]}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
  buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

gradlePlugin {
  plugins {
    create("kotlinIrPluginTemplate") {
      id = rootProject.extra["kotlin_plugin_id"] as String
      displayName = "Kermit Log Stripper"
      description = "Kermit Log Stripper"
      implementationClass = "co.touchlab.kermit.stripper.KermitStripperGradlePlugin"
    }
  }
}
