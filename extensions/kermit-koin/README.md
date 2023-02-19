# Kermit-Koin Integration

The Kermit-Koin module provides a way to add multiplatform logging to your Koin injection.

Firstly, add the gradle dependency to your project.  Typically you would be configuring a common 
set of Koin modules and starting Koin in your shared source set, 

```kotlin
sourceSets {
    commonMain {
        dependencies {
            implementation("co.touchlab:kermit-koin:x.y.z")  // Add the latest version
        }
    }
}
```

Then add the Kermit logger when you start your Koin application - 

```kotlin
val koinApplication = startKoin {
  modules( ... )
  
  val kermit = Logger.withTag("koin")
  logger(KermitKoinLogger(kermit))
}
```