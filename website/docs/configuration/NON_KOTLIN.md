# Non-Kotlin Environments

You can call Kermit from non-Kotlin code, but because of the way interop works, calling from those environments won't let you use default parameters. That will make calling Kotlin fairly verbose.

Earlier versions of Kermit were designed to be Swift-friendly, but that sacrificed the Kotlin-friendly API, and since the primary use-case is calling Kermit from Kotlin, that seemed like a bad compromise.

We've added a module called `kermit-simple` which adds explicit call signatures to the API, to provide for the cases where default parameters make more sense in a Kotlin context.

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                // etc
                implementation("co.touchlab:kermit:{{LATEST_GITHUB_VERSION}}")
            }
        }

        val iosMain by getting {
            dependencies {
                // etc
                api("co.touchlab:kermit-simple:{{LATEST_GITHUB_VERSION}}")
            }
        }
    }

    cocoapods {
        framework {
            export("co.touchlab:kermit-simple:{{LATEST_GITHUB_VERSION}}")
        }
    }
}
```

 You will need to export `kermit-simple`, as in the example above.

 From Swift, this will allow you to call methods without all of the parameters explicitly added.

 ```swift
 let log = // Get a Logger instance
log.i(messageString: "Try a log")
log.w(messageString: "Throw", throwable: someException)
```
