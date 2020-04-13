# Kermit

Kermit is a Kotlin Multiplatform logger that can support multiple platform and customizeable loggers. The library not only supports custom built loggers but also comes equiped with prebuilt loggers for Logcat, NSLog, and kotlin logging.

## Usage

Kermit contains two essential objects: `Kermit` itself and `Logger` objects. 
The `Kermit` object is constructed with a list or single `Logger` object, and handles logging to those loggers. 

The Kermit object is passed in `Logger` objects on initialization, it then logs to all these objects when it is called. `Kermit` is called similarly to Logcat, in that you call functions based on the severity of the log.



```
      val kermit = Kermit(LogcatLogger())
      kermit.i("CustomTag", optionalThrowable) { "Message" }
```



### Loggers

`Loggers` are passed into Kermit and are used to handle what happens to the logs. They support several levels of Severity:

* `v()` - Verbose
* `d()` - Debug
* `i()` - Info
* `w()` - Warn
* `e()` - Error
* `wtf()` - Assert

#### Prebuilt Loggers

By default Kermit comes with prebuilt loggers for Kotlin/Native Development.

* `CommonLogger` - Uses println to send logs in Kotlin
* `LogCatLogger` - Uses LogCat to send logs in Android
* `NSLogLogger`  - Uses NSLog to send logs in iOS

These can be created and passed into the Kermit object during initialization.

#### Custom Logger

If you want to customize what happens when a log is received, you can create a customized logger. For a simple `Logger` you would override the `log` method, which handles all logs of every Severity.

```
fun log(severity: Severity, message: String, tag: String? = null, throwable: Throwable? = null)
```

For a more nuanced implementation you can override each severity function specifically.

```
override fun e(message: String, tag: String? = null, throwable: Throwable? = null){
    super.e(message, tag, throwable)  // This will call the log function, you don't need this
    sendToServer(message,tag,throwable)
}

```

## Sample

Kermit comes with a Sample Multiplatform project which gives a brief example of how it can be used in both Android and iOS. 

TODO - Explain how to build project.

