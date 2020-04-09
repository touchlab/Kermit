plugins {
    id("com.android.application") version "3.6.1"
    kotlin("android") version "1.3.71"
    kotlin("android.extensions") version "1.3.71"
}

repositories {
    mavenLocal()
    google()
    mavenCentral()
    jcenter()
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.0.1"
    }
    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
    buildTypes {
        getByName("release")  {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(project(":shared"))
    implementation(kotlin("stdlib"))
    implementation("androidx.core:core-ktx:1.2.0")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("com.google.android.material:material:1.1.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.navigation:navigation-fragment-ktx:2.2.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.2.1")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")
}


