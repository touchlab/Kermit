# Custom API

The main module most users will include is `kermit`. However, much of the configuration and other underlying functionality has been moved to `kermit-core`. The main `kermit` module is a relatively thin API layer that sits on top of `kermit-core`.

Over the past few years, we've had feedback about the API design choices made in Kermit. Some of that feedback makes sense and has been incorporated. Other feedback is really just personal preference, and would conflict with the existing design.

Rather than trying to make everybody happy, we've structured Kermit's modules so you can create your own API interface on top of the underlying `kermit-core` module. That will allow you to use compatible extensions like Crashlytics and Bugsnag, but interface with the underlying Kermit system with whatever API you prefer.

For example, if you wanted a very simple logger with only two methods, you would make `kermit-core` a dependency rather than `kermit`, and write your own implementation:

```kotlin
object MyLogger : BaseLogger(loggerConfigInit(platformLogWriter())) {
    fun info(message: String){
        log(Severity.Info, "MyLogger", null, message)
    }
    
    fun error(message: String, throwable: Throwable){
        log(Severity.Error, "MyLogger", throwable, message)
    }
}
```

For a more complex example, see the implementation of `co.touchlab.kermit.Logger` in `kermit`.