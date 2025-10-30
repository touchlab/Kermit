# Kermit-Ktor Integration

The Kermit-Ktor module provides a way to add multiplatform logging to your Ktor client.

Firstly, add the gradle dependency to your project.

```kotlin
sourceSets {
    commonMain {
        dependencies {
            implementation("co.touchlab:kermit-ktor:x.y.z")  // Add the latest version
        }
    }
}
```

Then add the Kermit logger when you create your Ktor client -

```kotlin
val koinApplication = startKoin {
  modules( ... )
  
  val kermit = Logger.withTag("koin")
  logger(KermitKoinLogger(kermit))
}
```