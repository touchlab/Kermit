include(":kermit")

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:${requested.version}")
                "co.touchlab.native.cocoapods" -> useModule("co.touchlab:kotlinnativecocoapods:${requested.version}")
            }
        }
    }
    repositories {
        google()
        gradlePluginPortal()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
enableFeaturePreview("GRADLE_METADATA")