# Design Philosophy

## Audience

Kermit is designed for use in project with an existing Kotlin Multiplatform Module. It is useful for centralizing logging in a way that can be called from both common and native code. It is not intended to be used in purely native applications. 

## Configuration

We decided to do all configuration at construction and keep the Kermit implementation immutable. The expectation is that configuration will happen on each platform and the Kermit instance will be injected/passed to the code which uses it. Scoping of Kermit instances must be handled by the user. For example, calling `withTag()` to get a new tagged instance for a particular class. 

One of the key reasons we made this choice is for multithreaded performance. Avoiding any requirement for locks in logging calls. What we loose is flexibility in updating the logger after creating it and a globally accessible instance of Kermit. We hope to include future enhancements which may include lazily creating a global instance or providing more configuration options

## Loggers

Kermit was designed to support composable, customizable log outputs. By extending the Logger interfaces, Kermit can log to any output. We've included samples of simple outputs but logging to your specific outputs, such as crash reporting, remote logging, analytics, will be more valuable. We hope to include more sample loggers to illustrate these uses.

## Log syntax

All logs are passed in via a function. This was done to avoid expensive logic or string formatting if the log ends up being unused do to logger's `isLoggable` conditions. 
All logs include a tag. If not set defaults will be used. This does not mean that loggers must make use of the tags in any way. 
Throwables can be handled however the logger chooses, but we do provide a platform aware `ThrowableStringProvider` which can be used to get a string representation of the throwable including cause and stack trace if possible for the platform. 