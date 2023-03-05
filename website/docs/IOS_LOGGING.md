---
sidebar_position: 26
---

# iOS Logging

There are three `LogWriter` implementations for iOS.

## XcodeSeverityWriter

This is the default `LogWriter`. It is designed for local development. Each severity is represented with an emoji. `Throwable` instances sent to this writer will be written with `println` rather than oslog because oslog trims long strings.

## OSLogWriter

This is the parent class of `XcodeSeverityWriter`. There is no emoji added for severity, and `Throwable` is sent as a string to oslog. This may trim exceptions. You can implement a custom version that writes each line of a stack trace to oslog, or whatever else you'd like to do. Override `logThrowable`.

## NSLogWriter

Legacy implementation using `NSLog`.