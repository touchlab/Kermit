# Temporary overview of Kermit updates

This is a temporary doc that will be a "quick and dirty" description of the changes made. The library is in flux, so I'd
like to avoid "formal" docs until things land in place (to save time?)

## Basic Parts

The `Logger` is the core logging coordinator. It holds and dispatches to LogWriter instances. `LogWriter` is what actually
writes to the various logs.

## Global Logging

The global logger is currently `KLogger`. We've been back and forth on naming. To discuss. For now, let's use that, but 
we should pick a "final" name soon. There are multiple considerations that have been going into it.

## Mutable Config

The global module creates a global, mutable config. Again, names may change a bit, but for now you can access that global 
config with:

```kotlin
KermitGlobal.defaultConfig
```

It has 3 vars which let you change parts of the global config.

To create local loggers with different tags from that config, you can do this

```kotlin
val log = KLogger.withTag("MyTag")
```

## Modules

`kermit-core` has all of the core stuff for an immutable, non-global log implementation.

`kermit` adds mutable config as well as a global object to enable global logging.

`kermit-bugsnag` adds a Budsnag `LogWriter`

`kermit-crashlytics` adds a Crashlytics `LogWriter`