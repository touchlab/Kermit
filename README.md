# Kermit <sub>the log</sub>

Kermit is a Kotlin Multiplatform logging utility with composable log outputs. The library provides prebuilt loggers for 
outputting to platform logging tools such as Logcat and NSLog.

> Check out [KaMP Kit](https://github.com/touchlab/KaMPKit) to get started developing for Kotlin Multiplatform

## Version Note - September 2021

Kermit is getting some updates. Docs and samples are also in the refresh process and may be a little outdated.

## Most Users Read This

If you don't care about the philosophy of logging, custom configurations, and especially if you're writing for native mobile (KMM), 
then you should just do the following.

### Add Dependency

The Kermit dependency should be added to your `commonMain` source set in your Kotlin Multiplatform module.

```kotlin
commonMain {
    dependencies {
        implementation(kotlin("stdlib-common"))
        implementation("co.touchlab:kermit:x.y.z") //Add latest version
    }
}
```

### Basic Config

Kermit will log to `println()` by default. To get platform-specific loggers (which you probably want), do this
somewhere on init:

```kotlin
Logger.setLogWriters(platformLogWriter())
```

### Log

```kotlin
Logger.i { "Hello World" }
```

The rest of the docs explain more detailed options, but at this point you should be able to log from common 
code.

## Basic Concepts

The basic components you'll need to be aware of are `Logger`, `LogWriter`, and `Severity`.

### Logger

The `Logger` takes log calls from your code and dispatches them to `LogWriter` instances. There are different methods
on `Logger` corresponding to different log `Severity` levels. In order of least to most severe: v(), d(), i(), w(), e(),
and a().

You configure the `Logger`, then call log methods on it. That's the basic interaction with Kermit.

### LogWriter

A `LogWriter` actually sends log messages to different log outputs. You add `LogWriter` instances to a `Logger`.

Kermit includes a `CommonWriter` and various platform-specific `LogWriter` instances. Through other modules, Kermit
also allows logging crash info to Crashlytics and Bugsnag.

You can create you own `LogWriter` instances of your own. See [LOG_WRITER](docs/LOG_WRITER.md)

### Severity

Severity levels follow common logging library patterns and should be generally familiar. You can control what will
and won't get logged based on severity. So, say you only want to log `Warn` and up, you can tell the logger. We'll
cover that more in [Configuration](#Configuration)

## Usage

You call logging methods on a `Logger` instance. There are methods for each severity level. Each call takes an optional 
`Throwable` instance, and a lambda which returns a string. The Logger will only evaluate
the lambda if there is an enabled log writer that will write.

In it's most basic form, logging looks like this:

```kotlin
Logger.i { "Hello World" }
```

If you are not familiar with the curly bracket syntax, that is a [trailing lambda with special syntax](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).
Again, that will not be evaluated if no log writer needs it. String creation can be relatively costly if you don't need it,
so Kermit will avoid creating the string if it is not needed.

The call above is on the global `Logger` instance. You can make all of your logging on the global instance, but for custom 
tags, and potentially for performance reasons, you can use local instances as well.

### Local

Local usage is basically the same in concept. You simply call the same method on a local instance.

```kotlin
val logger = Logger.withTag("MyLogger")
logger.i { "Hello World" }
```

You can supply a different tag for the logger through global config, but also through local instances. This is more
meaningful in an Android context. However, there are also potential performance related reasons for local loggers. 
See [PERFORMANCE](PERFORMANCE.md) for more info.

## Configuration

You can configure three parameters for Kermit: `LogWriter` instances, default tag, and minimum severity.

### LogWriter Instances

By default, only the `CommonWriter` is enabled. You can swap other `LogWriter` instances. The most common scenario
is platform-default. There is a convenience function for that.

```kotlin
Logger.setLogWriters(platformLogWriter())
```

For more fine-grained control, you can supply log writers individually.

### Default Tag

The default tag is the tag used while logging if you have no changed a Logger-specific tag. By default, it is "Kermit".
You can change the default global tag with:

```kotlin
Logger.setDefaultTag("MyTag")
```

### Minimum Severity

To avoid logging lower level statements, you can set a minimum severity. This will prevent evaluating log 
message lambdas for those severities. To configure the global minimum severity, add:

```kotlin
Logger.setMinSeverity(Severity.Warn)
```

You may only want to turn this on in production, or by some other flag. Be careful, as it'll be easy
to turn this on and forget, then not see debug log statements. For that reason, it is probably best left
alone unless in a production situation.

### Local Configuration

The configuration above is on the global instance. For a number of reason, you may want a local `Logger` instead.
We provide a static config instance for situations where you con't need mutable config or global logging.

```kotlin
val logger = Logger(StaticConfig(minSeverity = Severity.Warn, defaultTag = "MyTag", loggerList = listOf(CommonWriter())))
logger.i { "Hello Local!" }
```

See [PERFORMANCE](docs/PERFORMANCE.md) for more info.

## Tags

Each `Logger` instance has a tag assoticated with it, which is initialzied by the default tag if none is provided. Tags
help categorize log statements. This feature is largely derived from Android, but can be useful in other contexts.

Tags are passed to `LogWriter` implementations which can decide how to use them (or ignore them). For example, `LogcatWriter` on Android
passes it along to Logcat's tag field.

You can override the global default tag [(see Default Tag)](#Default-Tag). You can also supply a default tag to `StaticConfig`.

To have a tag other than default, create a new `Logger` instance with:

```kotlin
val newTagLogger = logger.withTag("newTag")
```

## iOS

Generally speaking, Kermit's SDK was designed to be called from Kotlin, but you can initialzie and call logging from any
platform that has interop with Kotlin. For iOS and Swift-specific considerations, see [IOS_CONSIDERATIONS](docs/IOS_CONSIDERATIONS.md)

## Sample

Kermit comes with a Sample Multiplatform project which gives a brief example of how it can be used in both Android and iOS. 
The example demonstrates logs being called from within common code as well as from each platform.

### OSLogLogger

The iOS sample also includes a custom logger for outputting using `os_log`. `os_log` is not currently available in 
Kotlin/Native and consequently could not be included as a prebuilt logger in the library, but does serve as a good 
demonstration of custom logger implementation.

## Primary Maintainer

[Michael Friend](https://github.com/mrf7/)

![Image of Michael](https://avatars.githubusercontent.com/u/16885048?s=140&v=4)
