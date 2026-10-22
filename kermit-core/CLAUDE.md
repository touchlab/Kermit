# kermit-core

The foundational layer of Kermit: severity model, `LogWriter` abstraction, config types, message formatting, all concrete platform log writers, and the expect/actual factories for platform defaults. It deliberately does **not** contain the public `Logger` class — that lives in `:kermit`. `BaseLogger` here is the extension point.

This split is intentional and user-facing: developers who want Kermit's core but their own logging API are directed to build on `kermit-core` (extending `BaseLogger`) rather than on `:kermit` — see <https://touchlab.co/kermit-custom-logger>. Keep that use case working: `BaseLogger` and the config/writer/formatter types must remain usable without `:kermit`.

Artifact: `co.touchlab:kermit-core`. Android namespace `co.touchlab.kermit.core`. All code is in package `co.touchlab.kermit`.

## Key files (commonMain)

- `BaseLogger.kt` — `open class BaseLogger(open val config: LoggerConfig)`. Inline `logBlock(...)`/`log(...)` gate on `minSeverity`; `processLog(...)` fans out to each `LogWriter` after `isLoggable`. `mutableConfig` throws `IllegalStateException` if the config isn't a `MutableLoggerConfig`.
- `Severity.kt` — `enum class Severity { Verbose, Debug, Info, Warn, Error, Assert }`. **Ordinal order is load-bearing** — min-severity comparisons use `ordinal`.
- `LogWriter.kt` — `abstract class LogWriter` with `open isLoggable(tag, severity)` and `abstract log(severity, message, tag, throwable?)`.
- `LoggerConfig.kt` — `interface LoggerConfig`; immutable `data class StaticConfig` (thread-safe by construction, defaults to one `CommonWriter`); `loggerConfigInit(vararg logWriters, minSeverity)` factory.
- `MutableLoggerConfig.kt` — mutable config interface + `expect fun mutableLoggerConfigInit(logWriters: List<LogWriter>)`. Comments there explain the perf cost of mutable cross-thread state.
- `MessageStringFormatter.kt` — pluggable formatting interface, `@JvmInline value class Tag` / `Message`, singletons `DefaultFormatter`, `NoTagFormatter`, `SimpleFormatter`. Platform writers pass `null` severity/tag when the native log API already carries them (Logcat, OSLog, console).
- `CommonWriter.kt` — `println`-based writer, the universal fallback.
- `ChunkedLogWriter.kt` — decorator splitting long messages (newline-boundary-aware, tailrec) + `LogWriter.chunked()` extension. Default max 4000 chars (Logcat's limit).
- `PlatformLogWriter.kt` — `expect fun platformLogWriter(messageStringFormatter = DefaultFormatter): LogWriter`.
- `ExperimentalKermitApi.kt` — `@RequiresOptIn` annotation gating experimental API.

## Source sets & expect/actual map

Hierarchy: default template plus a custom `commonJvm` group (Android + JVM) and a `jsAndWasmJs` group (from the `wasm-setup` convention plugin).

Three expect declarations, with actuals:

1. **`platformLogWriter()`** — `androidMain` → `LogcatWriter` (falls back to `CommonWriter` if `android.util.Log` throws, e.g. plain unit tests); `jvmMain` → `SystemWriter` (stdout, stderr for `Error`+); `appleMain` → `XcodeSeverityWriter` (OSLog subclass with emoji severity prefixes; prints stack traces via `println` because oslog truncates); `androidNativeMain` → `AndroidNativeLogWriter` (`__android_log_print`); `linuxMain`/`mingwMain` → `CommonWriter`; `jsAndWasmJsMain` → `ConsoleWriter`.
2. **`mutableLoggerConfigInit(...)`** — `commonJvmMain` → `JvmMutableLoggerConfig` (`@Volatile` + `synchronized`); `nativeMain` → `AtomicMutableLoggerConfig` (`kotlin.concurrent.AtomicReference`); `jsAndWasmJsMain` → `JsMutableLoggerConfig` (plain fields — single-threaded).
3. **`internal expect object ConsoleActual : ConsoleIntf`** — `jsMain` (direct `console.*`) and `wasmJsMain` (`@JsFun` externals). Note the pattern: an internal *interface* (`ConsoleIntf`) carries the behavior; expect/actual is only the tiny platform binding.

Apple writers in `appleMain`: `OSLogWriter` (subsystem/category/publicLogging options), `XcodeSeverityWriter`, `NSLogWriter` (legacy).

## cinterop (Apple)

`src/nativeInterop/cInterop/os_log.def` contains an inline C shim (`kermit_darwin_log_create`, `kermit_darwin_log_with_type`, `kermit_darwin_log_public_with_type`) wired for every Apple target in `build.gradle.kts`. Two reasons it exists: (1) `os_log_t` has a type mismatch between cinterop and Kotlin/Native, worked around with an opaque `void*`; (2) the `%{public}s` format specifier must be a compile-time C string constant, so public vs. private logging needs two separate C functions — you cannot fold them into one with a Kotlin-side flag.

## Editing guidance

- This module is the reference implementation of the repo's KMP patterns: behavior behind common interfaces (`MessageStringFormatter`, `ConsoleIntf`, internal `DarwinLogger`), expect/actual confined to factory functions (`platformLogWriter`, `mutableLoggerConfigInit`) and minimal bindings, with every concrete writer public and constructor-configurable.
- Adding a new `LogWriter`: subclass `LogWriter` in the appropriate source set, make it `open` with constructor config (formatter at minimum), and only touch `platformLogWriter` actuals if the default for that platform should change.
- Thread safety: any new shared mutable state needs the per-platform treatment (`@Volatile`+`synchronized` on commonJvm, `AtomicReference` on native, plain on JS).
- Hot-path code (`logBlock`/`log`) is `inline` on purpose — the message lambda must not be evaluated unless severity passes.
- Public API changes require `./gradlew apiDump` (dumps in `api/`); binary-compat validation fails the build otherwise. New unstable API: annotate `@ExperimentalKermitApi` (excluded from validation).
- Tests: `commonTest`, plus `androidUnitTest` (Robolectric, `LogcatLoggerTest`), `appleTest` (`OSLogWriterTest`), `jsAndWasmJsTest` (`ConsoleWriterTest`). Test deps include `:kermit-test`, stately, testhelp.
