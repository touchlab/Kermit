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

The local and global loggers are basically the same. Which you use is mostly personal preference, with a bit of performance consideration.

Our pattern has been to inject loggers into components, with the tag applied at that point, rather than specify the tag in each call. In fact, the earlier Kermit designs mandated this rather than making a tag param available.

```kotlin
single { 
        BreedCallbackViewModel(
            get(), 
            getWith("BreedCallbackViewModel") // Convenience function to get a Logger with a tag set
        ) 
    }
```

The code above is from the [KaMP Kit Koin config](https://github.com/touchlab/KaMPKit/blob/main/shared/src/iosMain/kotlin/co/touchlab/kampkit/KoinIOS.kt#L34). There were some historical and technical reasons for that design. See [DESIGN](DESIGN.md) for more details.

To create your own logger rather than use the global logger, just create an instance with the config specified.

```kotlin
val baseLogger = Logger(
    config = loggerConfigInit(platformLogWriter(NoTagLogFormatter), minSeverity = Severity.Info),
    tag = "MyAppTag"
)

val anotherTag = baseLogger.withTag("AnotherTag")
```

There is a *slight* performance boost with this kind of configuration. Because this is not a mutable config, the internals don't need to be thread safe. Mutable config would look like this:

```kotlin
val baseLogger = Logger(
    config = mutableLoggerConfigInit(platformLogWriter(NoTagLogFormatter), minSeverity = Severity.Info),
    tag = "MyAppTag"
)

val anotherTag = baseLogger.withTag("AnotherTag")
```

Notice `mutableLoggerConfigInit`. The only major functional difference is you can change your config.

```kotlin
baseLogger.mutableConfig.minSeverity = Severity.Debug
baseLogger.mutableConfig.logWriterList = listOf(SomeCustomWriter())
```

In general, that's not something most apps will need, so it's better to avoid the mutable config. The global logger needs to be mutable because you need to change it after the code starts.

If you really like global access, but want static config, you can just create your own global logger.

```kotlin
object MyLogger : Logger(
    config = loggerConfigInit(
        platformLogWriter(NoTagLogFormatter),
        minSeverity = Severity.Info
    ),
    tag = "MyAppTag"
)

fun hello(){
    MyLogger.i { "Hello" }
}
```

### Message Formatting

To make message formatting more uniform and flexible, many of the `LogWriter` instances can take a `LogFormatter` parameter. This allows you to control how the tags and log messages are formatted.

We have defined a few standard `LogFormatter` instances:

`DefaultLogFormatter` - This is the standard format that all compatible `LogWriter` instances get by default. Messages are formatted as follows:

```
"Info: (MyTag) A log message"
```

`NoTagLogFormatter` - Tags are really an Android convension. Other platforms can feel cluttered with them. You can simply ignore them with this formatter instance. Messages are formatted as follows:

```
"Info: A log message"
```

`SimpleLogFormatter` - This formatter skips tags and severity. It just prints the message.

```
"A log message"
```

Not all `LogWriter` instances take a formatter argument. Notably, Android's `LogcatWriter` does not format it's own messages. Instead, it routes calls to the appropriate severity method, and passes the tag argument to Logcat.

To simplify setting platform `LogWriter` instances, you can pass a `LogFormatter` to `platformLogWriter`. Just FYI, Android will ignore the `LogFormatter` because it calls `Logcat` log methods directly.

```kotlin
platformLogWriter(NoTagLogFormatter)
```

## Non-Kotlin Callers

The api design of  `Logger` favors calling from Kotlin. Specifically, to avoid having a huge number of methods, the api makes use of default parameters. This is great in Kotlin but terrible when calling from other languages. For example, calling a log statement from Swift directly on the `Logger` api would look like this:

```swift
log.i(tag: log.tag, throwable: nil) {"A log"}
```

All default params need to be provided.

The original design of the `Logger` api made an attempt at compromise that would work everywhere. However, the redesign has a Kotlin-friendly api surface defined in the `kermit` module, and another module called `kermit-nkt` that you can export to other languages.

> `nkt` stands for "Not Kotlin".

We'll focus on calling from swift in these examples. You export the nkt module by adding it as an api dependency:

```kotlin
commonMain {
  dependencies {
    api("co.touchlab:kermit-nkt:{{LATEST_GITHUB_VERSION}}") //Add latest version
  }
}
```

Then export the dependency to the generated framework:

```kotlin
cocoapods {
    summary = "Sample for Kermit"
    homepage = "https://www.touchlab.co"
    framework {
        export("co.touchlab:kermit-nkt:{{LATEST_GITHUB_VERSION}}") //Add latest version
    }
}
```

If configuring frameworks directly, it would look more like this:

```kotlin
ios {
  binaries {
    framework {
      export("co.touchlab:kermit-nkt:{{LATEST_GITHUB_VERSION}}") //Add latest version
    }
  }
}
```

To call the global logger from Swift, call `LoggerKt`, select the severity, then select the method:

```swift
LoggerKt.i.log(messageString: "Hello")
LoggerKt.i.log(tag: "MyTag") { "Hello Again" }
```

See [samples/sample](https://github.com/touchlab/Kermit/tree/main/samples/sample) for a configured example.

## Crash Reporting

Kermit includes crash reporting implementations for Crashlytics and Bugsnag. These will write breadcrumb statements to
those crash reporting tools, and can be triggered to report unhandled crash reports when there's an uncaught Kotlin
exception.

> Read our blog post about Kermit and Crashlytics: https://touchlab.co/kermit-and-crashlytics/

Crashlytics [docs](crashlytics/README.md) and [sample](https://github.com/touchlab/Kermit/tree/main/samples/sample-crashlytics).

Bugsnag [docs](bugsnag/README.md) and [sample](https://github.com/touchlab/Kermit/tree/main/samples/sample-bugsnag).

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
        classpath("co.touchlab:kermit-gradle-plugin:{{LATEST_GITHUB_VERSION}}")
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
