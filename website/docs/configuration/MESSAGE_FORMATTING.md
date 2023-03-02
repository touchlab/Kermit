# Message Formatting

To make message formatting more uniform and flexible, many of the `LogWriter` instances can take a `MessageStringFormatter` parameter. This allows you to control how log messages are presented and apply common formatting.

:::info TL;DR

If you don't want tags to be printed in your iOS or JS log statements, give your `LogWriter` instances `NoTagFormatter`. This will suppress printing tag in log message strings. Tag will still be sent to Android log messages.

For the global `Logger` and the platform `LogWriter`, config would look like the following:

```kotlin
Logger.setLogWriters(platformLogWriter(NoTagFormatter))
```

For most use cases, this is all you need to know about formatters. The rest of this doc explains them in more detail.

:::

## Log Message Components

There are 3 parts to a message: Severity, Tag, and the Message itself. All loggers accept a log message string, but support for severity and tag vary between systems. Here is a breakdown of support for a few commonly used logging systems.

| System             | Severity | Tag |
|--------------------|----------|-----|
| Android            | ✅       | ✅  |
| OSLog (iOS)        | ✅       | ❌  |
| JS-Console         | ✅       | ❌  |
| SystemWriter (jvm) | ❌       | ❌  |
| Common (println)   | ❌       | ❌  |

The main purpose of `MessageStringFormatter` is to manage situations where the logging system does not support either Severity or Tag. The `LogWriter` instance will pass null's to the `MessageStringFormatter` in cases where the `LogWriter` natively supports Severity or Tag.

For example, the `DefaultLogFormatter` will result in the following message strings depending on platform:

```kotlin
// Log call
Logger.w(tag = "ATag") { "A Log Message" }

// Android
// 'A Log Message'

// OSLog
// '(ATag) A Log Message'

// Common
// 'Warn: (ATag) A Log Message'
```

:::info

You *can* use `MessageStringFormatter` to implement custom message strings. Maybe, say, add a special date format to each line. However, Kermit uses them to manage Severity and Tag for systems that don't natively support those constructs. Note: the Android `LogcatWriter` does not currently use a `MessageStringFormatter`. You would need to create a custom implementation to support that.

:::

## Implementations

Kermit provides a few implementations out of the box.

### `DefaultLogFormatter`

This is the standard format that all compatible `LogWriter` instances get by default. Messages are formatted as in the examples above.

### `NoTagFormatter`

Tags are really an Android convention. Other platforms can feel cluttered with them. You can simply ignore them with `NoTagFormatter`. Logging will function the same for Android, and other platforms will ignore the Tag.

### `SimpleLogFormatter`

This formatter skips tags and severity. It just prints the message.

## Configuration

To simplify setting platform `LogWriter` instances, you can pass a `MessageStringFormatter` to `platformLogWriter`.

```kotlin
platformLogWriter(NoTagLogFormatter)
```