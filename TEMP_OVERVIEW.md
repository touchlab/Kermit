# Temporary overview of Kermit updates

This is a temporary doc that will be a "quick and dirty" description of the changes made. The library is in flux, so I'd
like to avoid "formal" docs until things land in place (to save time?)

## Basic Parts

The `Logger` is the core logging coordinator. It holds and dispatches to LogWriter instances. `LogWriter` is what actually
writes to the various logs.

## Global Logging

The companion object for `Logger` is the global logger. It extends the `Logger` class, and so gets the logging functions
defined there. It also adds helper functions to modify the default "global" logging config.

```kotlin
Logger.i { "Hello world!" }
```

## Global Config

Global config can be set directly on `Logger`

```kotlin
Logger.setLogWriters(LogcatWriter())
Logger.setMinSeverity(Severity.Warn)
```

## Static Config

Static config is better for performance reasons. You cannot access it globally, but 
can pass it around and create new instances with different tags

```kotlin
val logger = Logger(StaticConfig(minSeverity = Severity.Debug, loggerList = listOf(LogcatWriter())))
```

## Tags

Kermit config has a default tag, which you can replace on child instances

### Global

```kotlin
val myLogger = Logger.withTag("MyTag")
```

### Local

```kotlin
val myLogger = aLogger.withTag("MyTag")
```

## Modules

`kermit` Base logging functionality with common platform loggers.

`kermit-bugsnag` adds a Budsnag `LogWriter`

`kermit-crashlytics` adds a Crashlytics `LogWriter`