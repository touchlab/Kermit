# kermit

The primary public API module — what most consumers depend on. Adds the `Logger` class with leveled logging functions, the global logger (the `Logger` companion object), and tag support, on top of `:kermit-core` (re-exported via `api`, so consumers see core types transitively).

Artifact: `co.touchlab:kermit`. Android namespace `co.touchlab.kermit`.

## Key file

`src/commonMain/kotlin/co/touchlab/kermit/Logger.kt` — essentially the whole module:

- `open class Logger(config: LoggerConfig, open val tag: String = "") : BaseLogger(config)` with `withTag(tag): Logger`.
- Inline leveled methods in two shapes each for `v/d/i/w/e/a`:
  - lambda message: `i(throwable = null, tag = this.tag) { "msg" }` (throwable-first is canonical; `@JvmOverloads`)
  - string message: `i("msg", throwable = null, tag = this.tag)`
  - Old tag-first overloads are kept as `@Deprecated` for source compatibility — don't remove them.
- `companion object : Logger(mutableLoggerConfigInit(listOf(platformLogWriter())), "")` — the global logger. It seeds itself with the platform-default writer via the core's expect/actual factory, overrides `tag` to read `defaultTag`, and adds mutation helpers: `setMinSeverity`, `setLogWriters` (list and vararg), `addLogWriter` (prepends), `setTag`.
- `internal expect var defaultTag: String`.

## Source sets & expect/actual

Same target matrix and hierarchy (`commonJvm`, `jsAndWasmJs` groups) as kermit-core. One expect: `defaultTag`, with three actuals mirroring core's thread-safety strategy:

- `commonJvmMain/DefaultsJVM.kt` — `@Volatile` field + `synchronized` setter
- `nativeMain/Defaults.kt` — `kotlin.concurrent.AtomicReference`
- `jsAndWasmJsMain/Defaults.kt` — plain field (single-threaded)

## Editing guidance

- The leveled methods are `inline` so lambda messages cost nothing when filtered out by `minSeverity` — keep them inline, and remember inline functions are **not** exported to ObjC/Swift (that's why `:kermit-simple` exists; if you add public API here, consider whether it needs a non-inline mirror there).
- This module's API is the library's stability contract. Prefer new overloads to signature changes; deprecate rather than remove. Run `./gradlew apiDump` after any public API change (dumps in `api/`).
- Tests in `commonTest` (`LoggerTest.kt`, `CustomGlobalLogger.kt`). Test deps: `kotlin("test")`, testhelp, `:kermit-test`.
