# Kermit-Coil Integration

The Kermit-Coil module lets a
[Kermit logger](https://kermit.touchlab.co/docs/configuration/LOGGER_SETUP) receive logs from
Coil 3 image loaders.

First, add the Gradle dependency to your project:

```kotlin
sourceSets {
    commonMain {
        dependencies {
            implementation("co.touchlab:kermit-coil:x.y.z") // Add the latest version
        }
    }
}
```

Then create `KermitCoilLogger` from a `LoggerConfig` and install it when creating your Coil image
loader. The config constructor is the preferred entry point and uses `"Coil"` as its default tag:

```kotlin
import co.touchlab.kermit.CommonWriter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.coil.KermitCoilLogger
import co.touchlab.kermit.loggerConfigInit
import coil3.ImageLoader

val coilLogger = KermitCoilLogger(
    config = loggerConfigInit(
        CommonWriter(),
        minSeverity = Severity.Info,
    ),
)

val imageLoader = ImageLoader.Builder(context)
    .logger(coilLogger)
    .build()
```

For convenience, you can construct the adapter from an existing `Logger`:

```kotlin
import co.touchlab.kermit.Logger
import co.touchlab.kermit.coil.KermitCoilLogger
import coil3.ImageLoader

val imageLoader = ImageLoader.Builder(context)
    .logger(KermitCoilLogger(Logger.withTag("Coil")))
    .build()
```

The adapter does not retain or log through the supplied `Logger` instance. This overload forwards
the logger's config and tag to the primary constructor, which creates a new internal logger with
its own mutable copy of the minimum severity and log writer list.

## Tags

`KermitCoilLogger` combines its base tag with the tag supplied by Coil. The separator defaults to
`"/"`, so a base tag of `"Coil"` and a Coil tag of `"RealImageLoader"` produce
`"Coil/RealImageLoader"`. If Coil supplies an empty tag, only the base tag is used.

Both the tag and separator can be customized when constructing the adapter from a config:

```kotlin
val coilLogger = KermitCoilLogger(
    config = config,
    tag = "Images",
    separator = ":",
)
```

When constructing the adapter from a `Logger`, its tag is used as the base tag and the separator
can still be customized:

```kotlin
val coilLogger = KermitCoilLogger(
    logger = logger,
    separator = ":",
)
```

## Changing the minimum log level

Like Coil's default logger, `KermitCoilLogger` exposes a mutable `minLevel` property:

```kotlin
import coil3.util.Logger as CoilLogger

coilLogger.minLevel = CoilLogger.Level.Debug
```

Regardless of which constructor is used, the adapter owns a mutable configuration copy. Changing
`minLevel` therefore affects only the adapter; it does not modify the original `Logger` or
`LoggerConfig`.

Coil levels map to the corresponding Kermit severities:

| Coil `Logger.Level` | Kermit `Severity` |
|---------------------|-------------------|
| `Verbose`           | `Verbose`         |
| `Debug`             | `Debug`           |
| `Info`              | `Info`            |
| `Warn`              | `Warn`            |
| `Error`             | `Error`           |

Coil has no equivalent to Kermit's `Severity.Assert`, so an `Assert` minimum severity is exposed
as Coil's `Logger.Level.Error`.
