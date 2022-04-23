# Kermit <sub>the log</sub>

Kermit is a Kotlin Multiplatform logging library.

It's primary purpose is to allow log statements from Kotlin code to be written to composable log outputs. Out of the box, the library defaults to platform-specific loggers such as Logcat and OSLog, but is easy to extend and configure.

> Check out [KaMP Kit](https://github.com/touchlab/KaMPKit) to get started developing for Kotlin Multiplatform


> ## Touchlab's Hiring!
>
> We're looking for a Mobile Developer, with Android/Kotlin experience, who is eager to dive into Kotlin Multiplatform Mobile (KMM) development. Come join the remote-first team putting KMM in production. [More info here](https://go.touchlab.co/careers-gh).

## Getting Started

### 1. Add Dependency

The Kermit dependency should be added to your `commonMain` source set in your Kotlin Multiplatform module.

```kotlin
commonMain {
    dependencies {
        implementation(kotlin("stdlib-common"))
        implementation("co.touchlab:kermit:x.y.z") //Add latest version
    }
}
```

### 2. Log

```kotlin
Logger.i { "Hello World" }
```

By default, Kermit includes a logger for each native platform that is configured for development. On Android it writes to Logcat, on iOS it writes to OSLog, and for JS it writes to console.

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
also allows logging crash info to Crashlytics, Bugsnag, and Sentry.

For more info on included `LogWriter` types, and to create your own, see [LOG_WRITER](docs/LOG_WRITER.md)

### Severity

Severity levels follow common logging library patterns and should be generally familiar. You can control what will
and won't get logged based on severity. So, say you only want to log `Warn` and up, you can tell the logger. We'll
cover that more in [Configuration](#Configuration)

## Usage

The primary logging artifact is [Logger](https://github.com/touchlab/Kermit/blob/kpg/api_reformat2/kermit/src/commonMain/kotlin/co/touchlab/kermit/Logger.kt). It defines the severity-level logging methods. It has a Kotlin-aware api relying on default parameters and lambda syntax. To log from non-Kotlin clients, see [NON_KOTLIN](NON_KOTLIN.md).

There are 2 sets of severity logging methods. One takes a `String` log message directly, the other takes a function parameter that returns a string. The function is only evaluated if the log will be written. Which you use is personal preference. They both will log to the same places, but the function paramter version may avoid unecessary `String` creation.

Each logging method has one of the two message forms (`String` or function param), and an optional `Throwable` and `String` tag argument.

In its most basic form, logging looks like this:

```kotlin
Logger.i { "Hello World" }
```

> If you are not familiar with the curly bracket syntax, that is a [trailing lambda with special syntax](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).
> Again, that will not be evaluated if no log writer needs it.

Some other examples with tags and `Throwable` params.

```ko
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

Platform-specific loggers can be configured to ignore tags on output, or you can customize their display easily. We'll discuss these options more in [Configuration](#Configuration).

## Configuration

Kermit basically has two modes. The global `Logger` instance, or an instance that is created and managed by your architecture (for example, injecting an instance into each component).

### Global Logger

The global `Logger` instance had configuration methods that allow you to set the `LogWriter` instances, the minimum severity, and set the default tag.

```kotlin
Logger.setLogWriters(platformLogWriter(NoTagLogFormatter))
Logger.setTag("MyTag")
```

### Local Logger

The local and global loggers are basically the same. Which you use is 

### LogWriter Instances

By default, only the `CommonWriter` is enabled. You can swap other `LogWriter` instances. The most common scenario
is platform-default. There is a convenience function for that.

```kotlin
Logger.setLogWriters(platformLogWriter())
```

For more fine-grained control, you can supply log writers individually. See [LOG_WRITER](docs/LOG_WRITER.md).

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
val logger = Logger(StaticConfig(minSeverity = Severity.Warn, loggerList = listOf(CommonWriter())))
logger.i { "Hello Local!" }
```

See [PERFORMANCE](docs/PERFORMANCE.md) for more info.

## Tags

Each `Logger` instance has a tag associated with it, which is initialized by the default tag if none is provided. Tags
help categorize log statements. This feature is largely derived from Android, but can be useful in other contexts.

Tags are passed to `LogWriter` implementations which can decide how to use them (or ignore them). For example, `LogcatWriter` on Android
passes it along to Logcat's tag field.

You can override the global default tag [(see Default Tag)](#Default-Tag).

To have a tag other than default, create a new `Logger` instance with:

```kotlin
val newTagLogger = logger.withTag("newTag")
```

## iOS

Generally speaking, Kermit's SDK was designed to be called from Kotlin, but you can initialize and call logging from any
platform that has interop with Kotlin. For iOS and Swift-specific considerations, see [IOS_CONSIDERATIONS](docs/IOS_CONSIDERATIONS.md)

## Samples

There are multiple sample apps showing various configurations.

## Crash Reporting

Kermit includes crash reporting implementations for Crashlytics and Bugsnag. These will write breadcrumb statements to
those crash reporting tools, and can be triggered to report unhandled crash reports when there's an uncaught Kotlin
exception.

> Read our blog post about Kermit and Crashlytics: https://touchlab.co/kermit-and-crashlytics/

Crashlytics [docs](kermit-crashlytics/README.md) and [sample](samples/sample-crashlytics).

Bugsnag [docs](kermit-bugsnag/README.md) and [sample](samples/sample-bugsnag).

## Testing

Kermit includes a test dependency, intended for use when testing application code that interacts with Kermit APIs but
doesn't want to write to actual logs. This includes a `TestLogWriter` which holds the string outputs of log statements,
and has APIs for asserting on what logs are present.

## Kermit Chisel

For some situations, disabling logging is desirable. For example, when building release versions of apps. You can
disable logging by defining minSeverity on the logging config, but you can also run a compiler plugin and strip out
logging calls entirely.

To run the log strip plugin, add the classpath to your buildscript:

```kotlin
buildscript {
    dependencies {
        classpath("co.touchlab:kermit-gradle-plugin:x.y.z")
    }
}
```

Then apply the plugin in your gradle file:

```kotlin
plugins {
    id("co.touchlab.kermit")
    //etc
}
```

By default, running the plugin does nothing. You should configure the plugin with a severity:

```kotlin
kermit {
    stripBelow = StripSeverity.Warn
}
```

Any log call below the configured severity will be removed. So, if you pass `Warn`, warn, error, and assert calls remain
but info and below are removed. There are some special values: `None` and `All`. `None` is default (removes nothing). `All` removes
all logging calls.

See the "sample-chisel" example. You can change the `stripBelow` and test various logging levels in the app.

In our production applications, we generally send error and warning level throwables to remote crash reporters, info level
is tracked in "breadcrumbs" for remote crash reporters. Debug and verbose are local-only. Sticking to that pattern, you could
configure your build as follows:

```kotlin
val releaseBuild: String by project

kermit {
    if(releaseBuild.toBoolean()) {
        stripBelow = StripSeverity.Info
    }
}
```

Add `releaseBuild=false` to `gradle.properties`, then pass in an override when building a release version.

Note: Chisel is new and configuration is likely to change in the near future.

## Primary Maintainer

[Michael Friend](https://github.com/mrf7/)

![Image of Michael](https://avatars.githubusercontent.com/u/16885048?s=140&v=4)
