# Performance

While performance of logging is not generally a major concern, logging should endeavor to impact performance as little 
as possible. Also, while most projects probably need not be concerned with logging performance, other projects may 
be very concerned with runtime performance, and all production large-scale apps might be better off disabling logging
below a certain level. Runtime performance impact has been a concern of Kermit design since the beginning and continues 
to drive various parts of the design and architecture.

TL;DR you probably don't need to worry about this much, but you can tweak a few things in Kermit to maximize performance
if desired.

## Mutable Configuration

Kermit originally had two design features that turned off potential users. No mutable config, and no global logger. 
Mutable config meant being able to change your log writers and severity levels at runtime, after being initially set. Global 
logging is a simple concept. You needed to creat your own instance of a `Logger` and pass it around, vs just being
able to do something like:

```kotlin
Logger.i { "Hello" }
```

They're actually related issues. To support global logging, you'd need to be able to set logging config after the global
instance was initialized. That implies mutable config.

What's wrong with mutable config? Well, mutable config requires thread-sharable state. On the JVM that implies at least
volatile. On Kotlin/Native, that means AtomicReference values (the new memory model may change things a bit, but we don't
know yet). Effectively that means each time you call a log method, you need to access atomic state. On the JVM this is 
arguably not much of an issue, but on native, accessing state back by atomics was determined to be a bit excessive. 
Because our primary use case was logging in production apps, creating a logging and passing it around with DI made 
global logging and mutable config unnecessary.

The lack of mutable config and global logging avoided the volatile/atomic situation altogether, and that was how the earlier
version of Kermit worked. You can still use Kermit in that way (and for native mobile apps, I would recommend that you do).
However, we've added global logging and mutable state by popular demand. Performance won't be an issue for the vast majority
of uses, but be aware that you can change how you configure logging to optimize performance if desired.

## Logging Calls

The logging calls take lambda args that return strings. The goal is to avoid evaluating those lambdas if you're not actually 
logging the string. We've added "regular" calls that take a string directly if that's preferred. That means instead of this:

```kotlin
Logger.i { "Hello" }
```

You can do this:

```kotlin
Logger.i("Hello")
```

The lambda log calls are inline, and if your minimum severity is configured such that Kermit should skip that call, it will 
do so without any deeper function calls. Just a check to minimum severity. In summary that means performance should be 
about as good as we can make it in normal situations.

## Log Call Stripping (AKA Chisel)

If you plan to disable logging in release builds, rather that doing so by config, you can run a compiler plugin to simply 
strip logging calls entirely. This will avoid the checking logic, as well as (slightly) reduce binary output size.