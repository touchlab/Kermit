plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("co.touchlab.native.cocoapods")
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
}

kotlin {
    version = "0.0.1"
    android()
    ios()
    jvm()

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
                api("co.touchlab:kermit:0.0.1")
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val androidTest by sourceSets.getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
            }
        }
        val iosMain by sourceSets.getting {
            dependencies {
                api("co.touchlab:crashkios:0.2.2")
            }
        }
        val jvmMain by sourceSets.getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
    }
    cocoapodsext {
        summary = "Sample for Kermit"
        homepage = "https://www.touchlab.co"
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}


