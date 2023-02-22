# Logger Setup

A `Logger` is just a class with a `LoggerConfig` instance and a default tag.

You can create your own instance of `Logger`, or configure and call the global `Logger` instance.

```kotlin
// Local
val log = Logger(
    loggerConfigInit(platformLogWriter(NoTagLogFormatter)), 
    "MyTag"
)
log.i { "Hello Kotlin" }

// Global
Logger.setLogWriters(platformLogWriter(NoTagLogFormatter))
Logger.setTag("MyTag")
Logger.i { "Hello Kotlin" }
```

Which method you choose is up to you, but obviously a local log instance will need to be made available for you code to call, while global is available everywhere. [For example](https://github.com/touchlab/KaMPKit/blob/main/shared/src/iosMain/kotlin/co/touchlab/kampkit/KoinIOS.kt#L34), our pattern has been to inject loggers into components, with the tag applied at that point, rather than specify the tag in each call.

```kotlin
single { 
        BreedCallbackViewModel(
            get(), 
            getWith("BreedCallbackViewModel") // Convenience function to get a Logger with a tag set
        ) 
    }
```

## LoggerConfig

`LoggerConfig` defines the `minSeverity`, below which log statements will be ignored, and `logWriterList`, the collection of `LogWriter` instances that will be written to.

```kotlin
interface LoggerConfig {
    val minSeverity: Severity
    val logWriterList: List<LogWriter>
}
```

When creating our own `Logger` instances above, we call `loggerConfigInit`. That is a convenience function to create a `StaticConfig` instance.

```kotlin
fun loggerConfigInit(vararg logWriters: LogWriter, minSeverity: Severity = DEFAULT_MIN_SEVERITY): LoggerConfig =
    StaticConfig(logWriterList = logWriters.toList(), minSeverity = minSeverity)
```

### Where to do config?

`platformLogWriter()` is an `expect`/`actual` factory function. You can call it in common code, and it will create a `LogWriter` designed for the platform the code is running on.

For more complex configuration, you'll either need platform-specific entry points, or possibly your own `expect`/`actual` factory function(s).

## StaticConfig vs MutableLoggerConfig

For most use cases, once your logger is set up, you won't need to change the config. `StaticConfig` has values that can't be changed once initializd. That is what you want when creating a local `Logger` instance.

The global `Logger` uses `MutableLoggerConfig`, because values need to be changed after that instance has been initialized. It is also possible that you have a use case where the config of your local `Logger` instance needs to be changed after the instance is created. In that case, you can use `MutableLoggerConfig` instead of `StaticConfig`.

:::info Why have StaticConfig at all?

`Logger` instances are thread-safe, so `MutableLoggerConfig` needs to be thread-safe. That means config fields are volatile on the JVM and Atomic on native platforms. While the performance impact is likely negligable, if you want to maximize performance, static is better.

:::

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
