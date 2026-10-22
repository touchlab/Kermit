# Kermit Production Sample

This sample demonstrates a production-oriented multiplatform configuration for Kermit targeting Android and iOS, featuring:
1. **Crash Reporting Integration:** Writing breadcrumbs and crash reports using `kermit-bugsnag`.
2. **Kermit Compiler Plugin (Log Stripping):** Using the `co.touchlab.kermit` Gradle plugin to strip out log calls and lambdas below a configured severity level at compile time.

## Kermit Compiler Plugin Configuration

In the root `build.gradle.kts`:
```kotlin
plugins {
    id("co.touchlab.kermit") version extra["KERMIT_VERSION"] as String apply false
}
```

In the multiplatform `shared/build.gradle.kts`:
```kotlin
import co.touchlab.kermit.gradle.StripSeverity

plugins {
    id("co.touchlab.kermit")
}

kermit {
    // Strips log calls below Info (i.e. Verbose and Debug) at compile time across all targets
    stripBelow = StripSeverity.Info
}
```

Any log call below `stripBelow` (e.g. `logger.v { ... }` or `logger.d { ... }`) is removed from the compiled bytecode / binary, preventing string allocations and log overhead in production. Available severity levels: `None`, `Verbose`, `Debug`, `Info`, `Warn`, `Error`, `Assert`, `All`.


