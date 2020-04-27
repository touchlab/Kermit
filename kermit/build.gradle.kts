plugins {
    id("com.android.library") version "3.6.1"
    kotlin("multiplatform") version "1.3.71"
}

apply(from = "../gradle/gradle-mvn-mpp-push.gradle")

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

val ideaActive = System.getProperty("idea.active") == "true"

repositories {
    google()
    mavenCentral()
    jcenter()
}

kotlin {
    android {
        publishAllLibraryVariants()
    }

    js() {
        browser()
    }

    if (ideaActive) {
        macosX64("ios")
    } else {
        macosX64()
        iosArm32()
        iosArm64()
        iosX64()

        linuxX64()
        linuxArm32Hfp()
        linuxMips32()

        watchosArm32()
        watchosArm64()
        watchosX86()
        tvosArm64()
        tvosX64()
        //    androidNativeArm32()
        //    androidNativeArm64()

        mingwX64()
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))
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
                implementation("androidx.test:runner:1.2.0")
                implementation("org.robolectric:robolectric:4.3.1")
            }
        }
        val nativeMain = sourceSets.maybeCreate("nativeMain").apply {
            dependsOn(commonMain.get())
        }
        val iosMain = sourceSets.maybeCreate("iosMain").apply {
            dependsOn(nativeMain)
        }
        val jsMain = sourceSets.maybeCreate("jsMain").apply {
            dependsOn(commonMain.get())
            dependencies {
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest = sourceSets.maybeCreate("jsTest").apply {
            dependsOn(commonTest.get())
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

        if (!ideaActive) {
            configure(
                listOf(
                    targets.findByName("iosX64"),
                    targets.findByName("iosArm32"),
                    targets.findByName("iosArm64"),
                    targets.findByName("macosX64"),
                    targets.findByName("watchosArm32"),
                    targets.findByName("watchosArm64"),
                    targets.findByName("watchosX86"),
                    targets.findByName("tvosArm64"),
                    targets.findByName("tvosX64")
                ).filterNotNull()
            ) {
                compilations.findByName("main")?.source(iosMain)

                sourceSets.findByName("iosTest")?.let {
                    compilations.findByName("test")?.source(it)
                }
            }

            configure(
                listOf(
                    targets.findByName("linuxX64"),
                    targets.findByName("linuxArm32Hfp"),
                    targets.findByName("linuxMips32"),
                    targets.findByName("mingwX64")
                ).filterNotNull()
            ) {
                compilations.findByName("main")?.source(nativeMain)

                sourceSets.findByName("nativeTest")?.let {
                    compilations.findByName("test")?.source(it)
                }
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
    }

    val main by sourceSets.getting {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
    }
}
