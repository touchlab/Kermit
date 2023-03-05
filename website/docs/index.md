---
id: intro
sidebar_position: 10
---

# Kermit <sub>the log</sub>

Kermit is a Kotlin Multiplatform logging library.

It's primary purpose is to allow log statements from Kotlin code to be written to composable log outputs. Out of the box, the library defaults to platform-specific loggers such as Logcat and OSLog, but is easy to extend and configure.

:::tip

Check out [KaMP Kit](https://github.com/touchlab/KaMPKit) to get started developing for Kotlin Multiplatform

:::

## Getting Started

Configuration for different environments can get more complex, but the default config out of the box is fairly simple.

### 1. Add Dependency

The Kermit dependency should be added to your `commonMain` source set in your Kotlin Multiplatform module.

```kotlin
commonMain {
    dependencies {
        implementation(kotlin("stdlib-common"))
        implementation("co.touchlab:kermit:{{LATEST_GITHUB_VERSION}}") //Add latest version
    }
}
```

### 2. Log

```kotlin
Logger.i { "Hello World" }
```

### Default LogWriter

By default, Kermit includes a LogWriter instance for each platform that is configured for **development**.

On Android it writes to Logcat, on iOS it writes to OSLog, and for JS it writes to console.

## Basic Concepts

The basic components you'll need to be aware of are `Logger`, `LogWriter`, and `Severity`.

### Logger

The `Logger` takes log calls from your code and dispatches them to `LogWriter` instances. There are different methods
on `Logger` corresponding to different log `Severity` levels. In order of least to most severe: v(), d(), i(), w(), e(), and a().

You configure the `Logger`, then call log methods on it. That's the basic interaction with Kermit.

```kotlin
Logger.i { "Hello World" }

try {
    somethingRisky()
}
catch(t: Throwable){
    Logger.w(t) { "That could've gone better" }
}
```

### LogWriter

A `LogWriter` actually sends log messages to different log outputs. You add `LogWriter` instances to a `Logger`.

Kermit includes a `CommonWriter` and various platform-specific `LogWriter` instances. Through other modules, Kermit also allows logging crash info to Crashlytics and Bugsnag.

For more info on included `LogWriter` types, and to create your own, see [LOG_WRITER](details/LOG_WRITER.md)

### Severity

Severity levels follow common logging library patterns and should be generally familiar. You can control what will and won't get logged based on severity. So, say you only want to log `Warn` and up, you can tell the logger. We'll cover that more in [Configuration](#Configuration)

## Usage

The primary logging artifact is [Logger](https://github.com/touchlab/Kermit/blob/kpg/api_reformat2/kermit/src/commonMain/kotlin/co/touchlab/kermit/Logger.kt). It defines the severity-level logging methods. It has a Kotlin-aware api relying on default parameters and lambda syntax.

:::info

To log from non-Kotlin clients, that don't support calling Kotlin's default parameters, see [NON_KOTLIN](configuration/NON_KOTLIN.md). A common use case would be calling Kermit from Swift or JS.

:::

For each severity, there are two methods. One takes a `String` log message directly, the other takes a function parameter that returns a string. The function is only evaluated if the log will be written. Which you use is personal preference. They both will log to the same places, but the function paramter version may avoid unecessary `String` creation and evaluation.

Here are what the `w` method definitions look like:

```kotlin
// Trailing function parameter
fun w(throwable: Throwable? = null, tag: String = this.tag, message: () -> String)

// String message parameter
fun w(messageString: String, throwable: Throwable? = null, tag: String = this.tag)
```

:::info

The function parameter is at the end of the function to support Kotlin's [trailing lambda syntax](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).

:::

In its most basic form, logging looks like this:

```kotlin
Logger.i { "Hello World" }
```

Some other examples with tags and `Throwable` params.

```kotlin
Logger.w("MyTag") { "Hello World $someData" }
// etc
Logger.e(ex) { "Something failed" }
// or
Logger.e("Something failed", ex)
```

### A Note About Tags

Tags are much more common on Android, as Logcat has tag arguments, and it is the default logger on Android. It would be difficult to have a Kotlin Multiplatform library without them, but they don't really fit into other platforms as easily.

Kermit's default tag is an empty string. You can supply a tag param to each log call, change the base default tag, or create a `Logger` instance with it's own tag. For example, create a field in a ViewModel with the tag set to the class name (a common Android pattern):

```kotlin
class MyViewModel:ViewModel {
	private val log = Logger.withTag("MyViewModel")
}
```

Platform-specific loggers can be configured to ignore tags on output, or you can customize their display easily. We'll discuss these options more in [Configuration](/configuration/index.md).
