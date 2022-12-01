# Kermit <sub>the log</sub>

Kermit is a Kotlin Multiplatform logging utility with composable log outputs. Out of the box, the library defaults to platform-specific loggers such as Logcat and OSLog, but is easy to extend and configure.

> Check out [KaMP Kit](https://github.com/touchlab/KaMPKit) to get started developing for Kotlin Multiplatform


> ## Subscribe!
>
> We build solutions that get teams started smoothly with Kotlin Multiplatform Mobile and ensure their success in production. Join our community to learn how your peers are adopting KMM.
 [Sign up here](https://go.touchlab.co/newsletter-gh)!

## Getting Help

Kermit support can be found in the Kotlin Community Slack, [request access here](http://slack.kotlinlang.org/). Post in the "[#touchlab-tools](https://kotlinlang.slack.com/archives/CTJB58X7X)" channel.

For direct assistance, please [contact Touchlab](https://go.touchlab.co/contactus-gh) to discuss support options.

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

### Log

```kotlin
Logger.i { "Hello World" }
```

The rest of the docs explain more detailed options, but at this point you should be able to log from common 
code.

### Defaults

By default, Kermit adds one Logger instance. The choice of default logger is basically the best option for local development. On Android, it is Logcat, for JS it just logs to console. On iOS, the logs go to OSLog but also get some visual style to help hightlight the severity.

Production deployments may want a different configuration.

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

For more info on included `LogWriter` types, and to create your own, see [LOG_WRITER](docs/LOG_WRITER.md)

### Severity

Severity levels follow common logging library patterns and should be generally familiar. You can control what will
and won't get logged based on severity. So, say you only want to log `Warn` and up, you can tell the logger. We'll
cover that more in [Configuration](#Configuration)

## Usage

You call logging methods on a `Logger` instance. There are methods for each severity level. Each call takes an optional 
`Throwable` instance, and a lambda which returns a string. The Logger will only evaluate
the lambda if there is an enabled log writer that will write.

In its most basic form, logging looks like this:

```kotlin
Logger.i { "Hello World" }
```

If you are not familiar with the curly bracket syntax, that is a [trailing lambda with special syntax](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas).
Again, that will not be evaluated if no log writer needs it. String creation can be relatively costly if you don't need it,
so Kermit will avoid creating the string if it is not being logged.

The call above is on the global `Logger` instance. You can make all of your logging on the global instance, or have local instances that are injected into your classes. We tend to use the latter, which accounts for some of Kermit's design decisions, but besides some small amount of performance boost, the choice between them is really down to personal preference.

### A Note About Tags

Tags are a complicating factor in the design. Currently tags are part of the Logger instance because we found the tag param to be kind of verbose and Android-specific. We also wanted to keep the number of api methods to a minimum because Swift (and others) can't handle default parameters, so each log statement requires all parameters in the call. However, if just using the global logger instance, having no tag parameter is a problem. We may be adding tag as a param but only for the global instance. Stay tuned (or comment in discussions).

### Local

Local usage is basically the same in concept. You simply call the same method on a local instance.

```kotlin
val logger = Logger.withTag("MyLogger")
logger.i { "Hello World" }
```

You can supply a different tag for the logger through local instances. This is more
meaningful in an Android context. As mentioned, there's also a slight performance advantage to local.
See [PERFORMANCE](docs/PERFORMANCE.md) for more info.

## Configuration

You can configure two parameters for LoggerConfig: `LogWriter` instances and minimum severity.

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

Crashlytics [docs](kermit-crashlytics/README.md) and [sample](samples/sample-crashlytics).

Bugsnag [docs](kermit-bugsnag/README.md) and [sample](samples/sample-bugsnag).

### Sentry

Sentry support exists but is experimental. We've had some reports of issues, so we may pull support until there are people available to look at it. Other users have it in production, so just be aware and verify that it works in your app.

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
