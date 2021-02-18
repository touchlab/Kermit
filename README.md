# Kermit <sub>the log</sub>

Kermit is a Kotlin Multiplatform logging utility with composable log outputs. The library provides prebuilt loggers for outputting to platform logging tools such as Logcat and NSLog.

> Check out [KaMP Kit](https://github.com/touchlab/KaMPKit) to get started developing for Kotlin Multiplatform

### Pre-release
Kermit is in a pre-release stage. We are still working to improve usage and future versions may change the public api.

### Design Philosophy
Read more about our api and architecture decisions here: [DESIGN](DESIGN.md)

## Installation

The Kermit dependency should added to your `commonMain` source set in your Kotlin Multiplatform module. 

```kotlin
commonMain {
    dependencies {
        implementation(kotlin("stdlib-common"))
        api("co.touchlab:kermit:0.1.7")
    }
}
```

Notice the use of the `api` configuration. This is to expose Kermit to the native Android & iOS code which includes your shared library. If using an iOS framework (including through CocoaPods) you will also need to transitively export kermit to make it available for use in swift. Learn more under "Exporting dependencies to binaries" [Here](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#building-final-native-binaries)

```kotlin
framework {
    export("co.touchlab:kermit:0.1.7")
    transitiveExport = true
}
```

When using cocoapods to package your shared module (we've found this to be the best way right now), you can use [Touchlab's cocoapods plugin](https://github.com/touchlab/KotlinCocoapods) to configure the framework. 

```kotlin
cocoapodsext {
    summary = "Sample for Kermit"
    homepage = "https://www.touchlab.co"
    framework {
        export("co.touchlab:kermit:0.1.7")
        transitiveExport = true
    }
}
```

## Usage

Logging calls are made to a `Kermit` instance which delegates logging to implementations of the `Logger` interface which are provided during instantiation. If no `Logger`s are provided, `CommonLogger`(which outputs to println) will be used by default.  

```kotlin
val kermit = Kermit(LogcatLogger(),CommonLogger(),CustomCrashLogger())
kermit.i("CustomTag", optionalThrowable) { "Message" }
```

`Kermit` provides convenience functions for each severity level:
* `v()` - Verbose
* `d()` - Debug
* `i()` - Info
* `w()` - Warn
* `e()` - Error
* `wtf()` - Assert

Each call takes optional parameters for tag and throwable and a function parameter which returns the string to be logged. The message function will only be invoked if there are loggers configured to log that particular message.

### Tags

Tags are passed to `Logger` implementations which can decide how to use them (or ignore them). `LogcatLogger` passes it along to Logcat's tag field. 

Tags can be set for logs in a few different ways:
* If no tag is specified, a default tag of "Kermit" will be used
* The default tag can be overridden in the `Kermit` constructor
* A copy of `Kermit` with a new default tag can be obtained with `kermit.withTag("newTag")`. This is handy for using a tag within a particular scope
* Each log can specify it's own tag with the optional tag parameter

### Loggers

Implementations of the `Logger` interface are passed into Kermit and determine the destination(s) of each log statement. 

#### Prebuilt Loggers

The Kermit library provides prebuilt loggers for common mobile cases:

* `CommonLogger` - Uses println to send logs in Kotlin
* `LogCatLogger` - Uses LogCat to send logs in Android
* `NSLogLogger`  - Uses NSLog to send logs in iOS

These can be created and passed into the Kermit object during initialization
```kotlin
val kermit = Kermit(LogcatLogger(),CommonLogger())
```

#### Custom Logger

If you want to customize what happens when a log is received, you can create a customized logger. For a simple `Logger` you only need to implement the `log` method, which handles all logs of every Severity.

```kotlin
class NSLogLogger : Logger() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        NSLog("%s: (%s) %s", severity.name, tag, message)
    }
}
```

You can optionally override the severity convenience functions if desired.

```kotlin
override fun v(message: String, tag: String, throwable: Throwable?) {
    Log.v(tag, message, throwable)
}
```

#### isLoggable

Custom loggers may also override `fun isLoggable(severity: Severity): Boolean`. Kermit will check this value before logging to this Logger

## Sample

Kermit comes with a Sample Multiplatform project which gives a brief example of how it can be used in both Android and iOS. The example demonstrates logs being called from within common code as well as from each platform.

### OSLogLogger

The iOS sample also includes a custom logger for outputting using `os_log`. `os_log` is not currently available in Kotlin/Native and consequently could not be included as a prebuilt logger in the library, but does serve as a good demonstration of custom logger implementation

## Primary Maintainer

[Michael Friend](https://github.com/kpgalligan/)

![Image of Michael](https://avatars.githubusercontent.com/u/16885048?s=140&v=4)
