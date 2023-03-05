---
sidebar_position: 20
---

# LogWriter

LogWriter takes care of deciding where to log the messages.

### Prebuilt LogWriters

By default `Kermit` provides default `LogWriter` for each platform

- `CommonWriter` - Uses println to send logs in Kotlin
- `LogcatWriter` - Uses LogCat to send logs in Android
- `OSLogWriter` - Uses os log to send logs in iOS
- `ConsoleWriter` - Uses console to log in JS

These can be created and passed into the `Logger` object during initialization

```kotlin
Logger.setLogWriters(listOf(LogcatWriter(), CommonWriter())
```

Selecting loggers for each platform can either be done with platform-specific entry points, or using an expect/actual factory function.

Kermit ships with a [default factory function](https://github.com/touchlab/Kermit/blob/main/kermit-core/src/commonMain/kotlin/co/touchlab/kermit/platformLogWriter.kt) that provides a `LogWriter` suited to local development.

```kotlin
package co.touchlab.kermit

expect fun platformLogWriter(messageStringFormatter: MessageStringFormatter = DefaultFormatter): LogWriter
```

You can implement a factory function for your project similar to the one above.

### Custom LogWriter

If you want to have a custom implementation to send logs to your own server, or a 3rd party tool or simply because default implementation doesn't fit your need, then you would need to extend the `LogWriter` class and provide your own instance.

For a simple `LogWriter` you only need to implement the `log` method, which handles all log messages.

Simple implementation would look like below,
```kotlin
class YourCustomWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        // Handle logging here
    }
}
```

Custom loggers may also override `isLoggable`. Kermit will check this value before logging to this LogWriter.

```kotlin
open fun isLoggable(tag: String, severity: Severity): Boolean = true
```

If your custom logger should only send messages in production, the implementation could look like the following:

```kotlin
// LogWriter
class YourCustomWriter(private val isProduction:Boolean) : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        // Do something custom
    }

    override fun isLoggable(tag: String, severity: Severity): Boolean = isProduction
}

// Usage 
val logWriter = YourCustomWriter(someProdFlag)
val logger = Logger(loggerConfigInit(logWriter))
```