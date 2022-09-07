plugins {
    `kotlin-dsl`
    kotlin("jvm") version embeddedKotlinVersion
}

// set the versions of Gradle plugins that the subprojects will use here
val kotlinVersion = "1.7.10"

val androidGradlePlugin = "4.1.2"
val binaryCompatibilityValidator = "0.8.0-RC"

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

    implementation("com.android.tools.build:gradle:$androidGradlePlugin")
    implementation("org.jetbrains.kotlinx:binary-compatibility-validator:$binaryCompatibilityValidator")

    implementation("com.github.gmazzo:gradle-buildconfig-plugin:3.1.0")

    implementation("com.vanniktech:gradle-maven-publish-plugin:0.18.0")
    implementation("co.touchlab:cklib-gradle-plugin:0.2.2")
}
