plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.maven.publish.gradlePlugin)
    implementation(libs.android.library.gradlePlugin)
}
