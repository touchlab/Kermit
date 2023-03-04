---
sidebar_position: 27
---

# Crash Reporting

Crash reporting is primarily handled with [CrashKiOS](https://crashkios.touchlab.co/). Kermit provides `LogWriter` instances to write breadcrumb/log statements to the crash reporting tools, and sending soft "handled" exceptions when `Throwable` instances are logged, based on configuration.

:::info

Earlier versions of Kermit implemented crash reporting directly, but logging and crash reporting are really different domains. Crash reporting support was moved back into CrashKiOS, and Kermit simply provides a logging interface into those tools.

:::

Kermit and CrashKiOS currently support [Firebase Crashlytics](https://firebase.google.com/) and [Bugsnag](https://www.bugsnag.com/).

## Crashlytics

See [Crashlytics Setup](CRASHLYTICS)

## Bugsnag

See [Bugsnag Setup](BUGSNAG)

## Configuring crash log writers

Both crash `LogWriter` implementations take 3 parameters:

### `minSeverity: Severity`

This is the minimum severity that will be logged to the crash reporter's breadcrumb cache. The default severity is `Info`. That means `Debug` and `Verbose` statements will be ignored.

### `minCrashSeverity: Severity?`

All log statements can take a `Throwable`. If you send a `Throwable` to a crash `LogWriter`, if the log statement itself is equal to or above `minCrashSeverity`, the `Throwable` will be sent as a soft/handled exception.

The default value is `Warn`, so all log statements with a `Throwable`, with log severity of `Warn` or higher, will create an exception report.

To disable sending exception reports, pass `null`.

### `messageStringFormatter: MessageStringFormatter`

See [Message Formatting](configuration/MESSAGE_FORMATTING.md) for details of how to format log message strings. `DefaultFormatter` is the default value.