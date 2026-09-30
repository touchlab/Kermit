# Using Kermit (Agent Guide)

This guide is for an AI agent (or developer) adding Kermit logging to a Kotlin Multiplatform, Android, JVM, JS, or native project. It covers which artifact to depend on, the core API, and each module's purpose. All artifacts are on Maven Central under group `co.touchlab`.

## Which artifact do I need?

| You want to... | Artifact |
|---|---|
| Log from Kotlin code (the normal case) | `co.touchlab:kermit` |
| Write a custom `LogWriter` library / build your own logging API on Kermit's core | `co.touchlab:kermit-core` |
| Call Kermit from Swift/Objective-C or JS with good ergonomics | `co.touchlab:kermit-simple` (in addition to exporting it in your framework) |
| Log to rolling files on disk | `co.touchlab:kermit-io` |
| Assert on log output in tests | `co.touchlab:kermit-test` (test dependency) |
| Send logs/handled exceptions to Firebase Crashlytics | `co.touchlab:kermit-crashlytics` |
| Send logs/handled exceptions to Bugsnag | `co.touchlab:kermit-bugsnag` |
| Route Koin's internal logging through Kermit / inject tagged loggers via Koin | `co.touchlab:kermit-koin` |
| Route Ktor HTTP client logging through Kermit | `co.touchlab:kermit-ktor` |

`kermit` re-exports `kermit-core`, and `kermit-simple` re-exports `kermit` — never depend on more than one of those three directly.

## Core concepts (kermit / kermit-core)

- **`Logger`** coordinates logging: it holds a `LoggerConfig` and dispatches to `LogWriter`s.
- **`LogWriter`** does the actual writing (Logcat, OSLog, console, file, Crashlytics, ...).
- **`Severity`**: `Verbose < Debug < Info < Warn < Error < Assert`. Config's `minSeverity` filters below-threshold calls before the message lambda is even evaluated (the leveled functions are inline).
- **`platformLogWriter()`** returns the idiomatic default writer for the current platform: `LogcatWriter` (Android), `XcodeSeverityWriter`/OSLog (Apple), `SystemWriter` (JVM), `ConsoleWriter` (JS/wasm), `AndroidNativeLogWriter` (Android NDK), `CommonWriter` (println; Linux/Windows). You can always construct a specific writer directly with custom configuration instead.

### Simplest usage — the global logger

```kotlin
import co.touchlab.kermit.Logger

Logger.i { "Hello world" }                       // lambda message (preferred; lazy)
Logger.e(throwable = ex) { "Request failed" }    // throwable-first
Logger.w("Plain string message")                 // string overload
```

Global configuration (mutable, slight perf cost per log call):

```kotlin
Logger.setMinSeverity(Severity.Warn)
Logger.setLogWriters(platformLogWriter(), MyCustomWriter())
Logger.addLogWriter(CrashlyticsLogWriter())      // prepends
Logger.setTag("MyApp")
```

### Preferred for real apps — injected logger with immutable config

```kotlin
val baseLogger = Logger(
    config = StaticConfig(
        minSeverity = Severity.Debug,
        logWriterList = listOf(platformLogWriter(), RollingFileLogWriter(fileConfig)),
    ),
    tag = "MyApp",
)
val logger = baseLogger.withTag("LoginViewModel")   // child logger with new tag
logger.d { "Login started" }
```

`StaticConfig` is immutable and thread-safe with no synchronization overhead; prefer it over the global mutable config, and inject `Logger` instances (e.g. via DI) rather than using the `Logger` companion, in production code.

### Custom log writers

Subclass `LogWriter` and implement `log(severity, message, tag, throwable)`; optionally override `isLoggable`. Use `MessageStringFormatter` (`DefaultFormatter`, `NoTagFormatter`, `SimpleFormatter`, or your own) for consistent formatting. Wrap any writer with `.chunked()` if the destination truncates long messages (default limit matches Logcat's ~4000 chars).

## Module purposes and usage notes

### kermit
The main API: `Logger`, leveled `v/d/i/w/e/a` functions (lambda and string overloads), `withTag`, and the global companion. This is the dependency for nearly all consumers.

### kermit-core
Foundation types only — `Severity`, `LogWriter`, `LoggerConfig`/`StaticConfig`/`MutableLoggerConfig`, `MessageStringFormatter`, platform writers, `platformLogWriter()`. Depend on this directly only when building a Kermit extension library or when you don't want the `Logger` class.

**Building your own logging API on Kermit:** if you want Kermit's core (writers, severity filtering, config, platform defaults) but your own logging API instead of the `Logger` class, extend `kermit-core` as the base — typically by subclassing `BaseLogger` and defining your own entry-point functions — rather than wrapping or forking `kermit`. See <https://touchlab.co/kermit-custom-logger> for a walkthrough.

### kermit-simple
For exposing Kermit through a Kotlin/Native framework to Swift/ObjC (or to JS). Kotlin `inline` functions aren't exported to ObjC, so this adds non-inline `Logger.v/d/i/w/e/a` extension overloads plus top-level global functions. Add it to your shared module and list it in the framework's `export(...)` configuration so Swift sees the API.

### kermit-io
`RollingFileLogWriter(RollingFileLogWriterConfig(logFileName = "app", logFilePath = path))` writes logs to `<name>.log`, rolling to `<name>-1.log` etc. on size (default 10MB, keep 5 files). Writes happen on a dedicated background thread; IO errors (e.g. iOS file protection while device locked) are tolerated and writing resumes automatically. Available on Android/JVM/Apple/Linux/Windows/Android Native — **not** JS/wasm.

### kermit-test
`@ExperimentalKermitApi`. Inject a `TestLogWriter` via `TestConfig`, then assert:

```kotlin
val writer = TestLogWriter(loggable = Severity.Verbose)
val logger = Logger(TestConfig(minSeverity = Severity.Debug, logWriterList = listOf(writer)))
// ... exercise code under test ...
writer.assertCount(1)
writer.assertLast { message == "Login started" && severity == Severity.Debug }
```

### kermit-crashlytics / kermit-bugsnag
`@ExperimentalKermitApi`. Add `CrashlyticsLogWriter()` / `BugsnagLogWriter()` to your writer list. Log messages become breadcrumbs (at/above `minSeverity`, default `Info`); throwables at/above `minCrashSeverity` (default `Warn`, `null` to disable) are reported as handled exceptions. Android + Apple targets only. Built on Touchlab's CrashKiOS; for iOS *unhandled* crash reporting also call `setCrashlyticsUnhandledExceptionHook` (from CrashKiOS) — see each module's README. The underlying Crashlytics/Bugsnag SDK must be set up in the app per its own docs.

### kermit-koin
Two uses: `startKoin { logger(KermitKoinLogger(Logger.withTag("koin"))) }` routes Koin's own diagnostics through Kermit; `kermitLoggerModule(baseLogger)` + `getLoggerWithTag("MyTag")` lets you inject tagged `Logger` instances through Koin scopes. Note: not available on `androidNative*`, `watchosArm32`, `watchosDeviceArm64`, or `linuxArm64` (Koin doesn't support them).

### kermit-ktor
```kotlin
HttpClient {
    install(Logging) {
        logger = KermitKtorLogger(Severity.Info, myKermitLogger)
        level = LogLevel.HEADERS
    }
}
```
All Ktor client log output flows to the given Kermit logger at the fixed severity. Very broad target support (matches Ktor client).

## Practical tips for agents

- Prefer lambda-message overloads (`logger.i { ... }`) — messages below `minSeverity` are never constructed.
- Prefer `StaticConfig` + injected loggers over mutating the global `Logger` companion; reserve the global for small apps and samples.
- Throwable goes **first** in the lambda overloads: `logger.e(ex) { "failed" }`. Tag-first overloads exist but are deprecated.
- Writers ship with sensible defaults via `platformLogWriter()`, but every concrete writer (`LogcatWriter`, `OSLogWriter(subsystem, category)`, `RollingFileLogWriter(config)`, ...) is public and constructible when you need specific configuration.
- `@ExperimentalKermitApi` types (test, crashlytics, bugsnag) require an opt-in: `@OptIn(ExperimentalKermitApi::class)` or the `-opt-in` compiler flag.
