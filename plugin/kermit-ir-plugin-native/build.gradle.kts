plugins {
  kotlin("jvm")
  kotlin("kapt")
//  id("org.jetbrains.dokka")
  id("com.vanniktech.maven.publish")
}

dependencies {
  compileOnly("org.jetbrains.kotlin:kotlin-compiler")

  kapt("com.google.auto.service:auto-service:1.0-rc7")
  compileOnly("com.google.auto.service:auto-service-annotations:1.0-rc7")
}

tasks.named("compileKotlin") { dependsOn("syncSource") }
tasks.register<Sync>("syncSource") {
  from(project(":kermit-ir-plugin").sourceSets.main.get().allSource)
  into("src/main/kotlin")
  filter {
    // Replace shadowed imports from plugin module
    when (it) {
      "import org.jetbrains.kotlin.com.intellij.mock.MockProject" -> "import com.intellij.mock.MockProject"
      else -> it
    }
  }
}