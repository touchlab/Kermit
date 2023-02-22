# Message Formatting

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