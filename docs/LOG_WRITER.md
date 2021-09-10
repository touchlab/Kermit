




















!!!!! EVERYTHING BELOW HERE WAS FROM THE ORIGINAL README !!!!!
There may be some info here that would be useful to update, but otherwise delete it. It is likely outdated. The
names are old, btw ("Logger" is now "LowWriter" and "Kermit" is now "Logger")


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
