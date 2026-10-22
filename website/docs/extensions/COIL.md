# Coil Integration

Kermit's Coil 3 integration allows you to pass in a Kermit logger instance to your Coil `ImageLoader`.

## Setup

### Add the dependency

```kotlin
commonMain {
    dependencies {
        implementation("co.touchlab:kermit-coil:{{LATEST_GITHUB_VERSION}}")
    }
}
```

### Add the logger to your ImageLoader

```kotlin
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

or alternatively

```kotlin
val imageLoader = ImageLoader.Builder(context)
    .logger(KermitCoilLogger(Logger.withTag("Coil")))
    .build()
```

This takes in a Kermit logger configuration, or alternatively a Kermit logger instance, to create the adapter from.
This should allow some flexibility in how you want to set up your logging.

### Customizing Tags

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

### Changing the minimum log level

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