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
