# kermit-coil

Adapter for Coil 3's `coil3.util.Logger`, so Coil image-loading logs flow through Kermit. Depends on the full `:kermit` module (implementation) plus `io.coil-kt.coil3:coil-core`.

Artifact: `co.touchlab:kermit-coil`. Package `co.touchlab.kermit.coil`. Stable API — `api/android/` and `api/jvm/` dumps are validated.

## Key files (commonMain only — no expect/actual)

`src/commonMain/kotlin/co/touchlab/kermit/coil/KermitCoilLogger.kt` — implements Coil's `coil3.util.Logger`:

- Primary constructor `(config: LoggerConfig, tag: String = "Coil", separator: String = "/")` — copies the config's writers and `minSeverity` into an internal `Logger`.
- Secondary constructor `(logger: KermitLogger, separator: String = "/")` — reuses that logger's config and tag.
- `minLevel` is backed by the internal logger's `minSeverity`, so Coil's level gate and Kermit's stay in sync.
- `log` skips calls where both message and throwable are null, and emits the Kermit tag as `"<kermitTag><separator><coilTag>"` (separator dropped if either tag is empty). The Coil level maps to the matching Kermit `Severity`.
- Used as `ImageLoader.Builder(context).logger(KermitCoilLogger(myKermitLogger))` (see `README.md`).

`CoilLoggerLevelBridge.kt` — internal `Level.toKermitSeverity()` / `Severity.toCoilLoggerLevel()` mappings (`Severity.Assert` maps to Coil `Error`, since Coil has no equivalent).

## Build notes

- Uses the `kermit.multiplatform-library` plugin; targets are declared in `build.gradle.kts`: Android, JVM, iOS, macOS, Linux, and web (JS nodejs + wasmJs). **Narrower than kermit-ktor** — no tvOS, watchOS, mingwX64, or androidNative. Keep parity with what Coil 3 supports when adding targets.
- Coil version is `coil` in `gradle/libs.versions.toml` (`coil-core`).

## Editing guidance

- Pure-common adapter over two stable interfaces — no expect/actual, no platform source sets; keep it that way.
- Configuration pattern: the primary constructor takes a `LoggerConfig` plus tag/separator; the `Logger` constructor is convenience only. New convenience should follow that shape — sugar on top, full configuration underneath.
- Stable-API module: public changes require `./gradlew apiDump` and prefer added overloads over signature changes (constructors use `@JvmOverloads`, so the jvm `.api` dump tracks every overload).
- When bumping Coil, verify the `coil3.util.Logger` interface and `Level` enum haven't changed (the bridge `when`s are exhaustive) and re-check the target matrix.
