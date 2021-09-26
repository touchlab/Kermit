# Performance

While performance of logging is not generally a major concern, logging should endeavor to impact performance as little 
as possible. Runtime performance impact has been a concern of Kermit design since the beginning and continues to drive
various parts of the design and architecture.

## Mutable Configuration

Kermit originally had two design features that turned off potential users. No mutable config, and no global logger. 
Mutable config meant being able to change your log writers and severity levels at runtime, after being set. Global 
logging is a simple concept. You needed to creat your own instance of a `Logger` and pass it around, vs just being
able to do something like:

```kotlin
Logger.i { "Hello" }
```

They're actually related issues. To support global logging, you'd need to be able to set logging config after the global
instance was initialized. That means mutable config.

What's wrong with mutable config? Well, mutable config requires thread-sharable state. On the JVM that implies at least
volatile. On Kotlin/Native, that means AtomicReference values (the new memory model may change things a bit, but we don't
know yet). Effectively that means each time you call a log method, you need to access atomic state. While perhaps not 
a major issue in practice, it would be best to avoid it if possible.

There are very few realistic use cases where you might need mutable config in a remote app scenario, and we decided to live
without global access initially, so earlier versions of Kermit had immutable state. That would avoid the volatile/atomic
situation altogether. You can still use Kermit in that way (and for native mobile apps, I would highly recommend that you do).
However, we added global logging and mutable state by popular demand. Just be aware of the implications.

## Logging Calls

The logging calls take lambda args that return strings. The goal is to avoid evaluating those lambdas if you're not actually 
logging the string. We've added "regular" calls that take a string directly if that's preferred.

The lambda log calls are inline, and if your minimum severity is configured such that Kermit should skip that call, it will 
do so without any deeper function calls. In summary that means performance should be about as good as we can make it
in normal situations. Of course, if performance is a concern, use static config as discussed above to avoid accessing thread-sharable
state.

## Log Call Stripping

If you plan to disable logging in release builds, rather that doing so by config, you can run a compiler plugin to simply 
strip logging calls entirely. This will avoid the checking logic, as well as (slightly) reduce binary output size.