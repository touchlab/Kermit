# Koin Integration

Kermit's Koin integation comes in two parts - a logger implementation that writes
Koin logger output to Kermit, and dependency injection helpers.

## Setup

### Add the dependency

```kotlin
commonMain {
    dependencies {
        implementation("co.touchlab:kermit-koin:{{LATEST_GITHUB_VERSION}}")
    }
}
```

### Register the logger with Koin

```kotlin
val koinApplication = startKoin {
    logger(
        KermitKoinLogger(Logger.withTag("koin"))
    )

    modules(/* modulesList */)
}
```

Obviously you will want to have initialized Kermit before registering a logger with Koin, and the 
tag you pass is optional.  That said, it's useful to tag the Koin output to be able to filter and
see what is going on.  Once you have registered the logger, all of the normal _Koin_ logging will 
be piped through to Kermit.  This is especially helpful when checking your module dependencies from
unit tests!

## Dependency Injection Helpers

The `kermitLoggerModule()` method returns a Koin module that declares a factory for logger instances.  If
you don't want to make use of the Koin factory, there's a `getLoggerWithTag()` extension method you can
call directly.

We prefer injecting logger instances rather than using the global `Logger` instance, especially when
we know we'll be unit testing a section of code.
 