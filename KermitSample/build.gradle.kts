buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.6.3")
        classpath(kotlin("gradle-plugin","1.3.72"))
        classpath("co.touchlab:kotlinnativecocoapods:0.6")
    }
}
allprojects{
    repositories{
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }
}