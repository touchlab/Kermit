# LogWriter

LogWriter takes care of deciding where to log the messages.

### Prebuilt LogWriters

By default `Kermit` provides default `LogWriter` for each platform

- `CommonWriter` - Uses println to send logs in Kotlin
- `LogcatWriter` - Uses LogCat to send logs in Android
- `NSLogWriter` - Uses NSLog to send logs in iOS
- `ConsoleWriter` - Uses console to log in JS

These can be created and passed into the `Logger` object during initialization
```kotlin
Logger.setLogWriters(listOf(LogcatWriter(), CommonWriter())
```
### Custom LogWriter

If you want to have a custom implementation to send logs to your own server, or a 3rd party tool or simply because default implementation doesn't fit your need, then you would need to extend the `LogWriter` class and provide your own instance.

For a simple `LogWriter` you only need to implement the `log` method, which handles all logs of every Severity.

Simple implementation would look like below,
```kotlin
class YourCustomWriter : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        // Handle logging here
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

Custom loggers may also override `fun isLoggable(severity: Severity): Boolean`. Kermit will check this value before logging to this LogWriter.

Simple example of that would be: If on your Android build, you have your own `LogWriter` and you want to handle logs only on `debug` build then you could do that using `isLoggable`

```kotlin
// Custom Implementation
class YourCustomWriter(private val shouldLog: Boolean) : LogWriter() {

    override fun isLoggable(severity: Severity): Boolean {
        return shouldLog
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        // Handle logging here
    }
}

// Usage 
val logWriter = YourCustomWriter(shouldLog = BuildConfig.DEBUG)
val logger = Logger(LoggerConfig.default.copy(logWriterList = listOf(logWriter)))
```