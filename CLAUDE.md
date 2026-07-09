# Kermit

Kermit is Touchlab's Kotlin Multiplatform (KMP) logging library. Group `co.touchlab`, version in `gradle.properties` (`VERSION_NAME`). Kotlin 2.2.0, AGP 8.12.3. Docs site: [kermit.touchlab.co](https://kermit.touchlab.co).

For how to *use* the library as a consumer (API examples, which artifact to depend on), see `AGENT-USAGE.md`. Each module also has its own `CLAUDE.md` with detailed editing guidance.

## Modules

| Module | Path | Purpose |
|---|---|---|
| `kermit-core` | `kermit-core/` | Foundation: `Severity`, `LogWriter`, `BaseLogger`, configs, formatters, all platform log writers, expect/actual factories. No public `Logger` class. Also the supported base for consumers building their own logging API on Kermit (see <https://touchlab.co/kermit-custom-logger>). |
| `kermit` | `kermit/` | The main artifact consumers use. Adds `Logger` (leveled `v/d/i/w/e/a` functions), the global `Logger` companion, tags. `api`-re-exports `kermit-core`. |
| `kermit-simple` | `kermit-simple/` | Non-inline API surface for Objective-C/Swift and JS consumers (inline functions don't export to ObjC headers). `api`-re-exports `kermit`. |
| `kermit-io` | `kermit-io/` | `RollingFileLogWriter` — size-rolling file logging built on kotlinx-io. No JS/wasm target. |
| `kermit-test` | `kermit-test/` | `TestLogWriter` + assertion helpers for testing log output. `@ExperimentalKermitApi`. |
| `kermit-crashlytics` | `extensions/kermit-crashlytics/` | `LogWriter` forwarding logs/exceptions to Firebase Crashlytics via CrashKiOS. `@ExperimentalKermitApi`. |
| `kermit-bugsnag` | `extensions/kermit-bugsnag/` | Same as crashlytics but for Bugsnag. Near-identical mirror module. `@ExperimentalKermitApi`. |
| `kermit-koin` | `extensions/kermit-koin/` | Koin logger bridge (`KermitKoinLogger`) + module/scope helpers for injecting tagged loggers. |
| `kermit-ktor` | `extensions/kermit-ktor/` | `KermitKtorLogger` adapter for the Ktor client `Logging` plugin. Widest target matrix of the extensions. |

Dependency chain: `kermit-core` ← `kermit` ← `kermit-simple`. Extensions depend on `kermit-core` (crashlytics, bugsnag) or `kermit` (koin, ktor). `kermit-io` and `kermit-test` depend on `kermit-core`.

`plugin/` (compiler/gradle plugins) is currently disabled — commented out in `settings.gradle.kts`. `TEMP_OVERVIEW.md` is an informal legacy overview; some examples in it use outdated parameter names — trust the source, not that doc.

## Build & test

Run everything from the repo root with `./gradlew`:

- `./gradlew build` — builds and tests all modules. `check` also runs `ktlintCheck`.
- `./gradlew ktlintCheck` / `ktlintFormat` — lint gate (ktlint 1.4.0, experimental rules on). CI fails on lint.
- `./gradlew apiDump` — regenerate binary-compatibility dumps (`api/` dir in each module). **Any public API change fails `build` until you run this.** `@ExperimentalKermitApi` symbols are excluded from validation.
- `./gradlew publishToMavenLocal -PRELEASE_SIGNING_ENABLED=false` — required before building samples.
- `./ci-test-samples.sh` — builds every sample (each sample in `samples/` is a standalone Gradle build with its own wrapper, consuming Kermit from mavenLocal).
- `./gradlew mingwX64Test` — what Windows CI runs.

Other build facts:
- Convention plugins live in the included build `convention-plugins/`: `kermit-publish` (Maven Central via vanniktech plugin), `kermit-jvm-target` (pins JVM 1.8), `wasm-setup` (adds `wasmJs` when `enableWasm=true` — it is `true` in `gradle.properties` — plus the `jsAndWasmJs` source-set group and `-Xexpect-actual-classes`).
- Target matrix (core modules): Android, JVM, JS (nodejs), wasmJs, full Apple matrix (macOS/iOS/watchOS incl. device arm64/tvOS), Linux x64/arm64, mingwX64, androidNative x4. Extensions vary — see their CLAUDE.md files.
- JS lock files are committed (`kotlin-js-store/`, `kotlin.js.yarn=false`). Regenerate with `kotlinUpgradePackageLock` / `kotlinWasmUpgradePackageLock` when JS deps change.
- Releases: bump `VERSION_NAME`, update `CHANGELOG.md`, then the manual `release` GitHub workflow (see `RELEASING.md`).

## Keeping this context current

When making a meaningful change to the library, update the relevant CLAUDE.md file(s) — and `AGENT-USAGE.md` if the consumer-facing API or module purposes change — as part of the same change. "Meaningful" means anything that would make the current docs wrong or incomplete: new or changed public API, new modules or targets, changed defaults, new expect/actual declarations, changed build commands or gates, or changed behavior that the docs describe (e.g. rolling/error-recovery semantics). Bug fixes and minor internal changes that don't affect anything the docs say do **not** require doc updates.

## KMP code guidance

When editing or adding code anywhere in this repo:

- **Prefer interfaces over expect/actual classes.** Model platform-varying behavior as a common interface with platform implementations (see `MessageStringFormatter`, `LoggerConfig`, the internal `ConsoleIntf`/`DarwinLogger` pattern). Reserve expect/actual for the narrow cases where an interface can't work.
- **Use expect/actual *factory functions* for defaults, structured so callers can still configure explicitly.** The canonical examples: `expect fun platformLogWriter(messageStringFormatter: MessageStringFormatter = DefaultFormatter): LogWriter` and `expect fun mutableLoggerConfigInit(logWriters: List<LogWriter>): MutableLoggerConfig`. The factory picks a sensible platform default, but the concrete writers/configs (`LogcatWriter`, `OSLogWriter`, `StaticConfig`, ...) remain public and directly constructible with full configuration. New defaults should follow the same shape: factory for convenience, public configurable type underneath.
- Thread safety for shared mutable state is handled per-platform: JVM/Android uses `@Volatile` + `synchronized`, Native uses `kotlin.concurrent.AtomicReference`, JS/wasm uses plain fields. Match this pattern (see `mutableLoggerConfigInit` actuals and `defaultTag`).
- Keep hot paths allocation-free where the existing code does: leveled log methods are `inline` with lambda messages, gated on `minSeverity` before evaluating the message.
- Public API changes require `apiDump` and a conscious binary-compatibility decision. Prefer adding overloads over changing signatures; the repo keeps `@Deprecated` compatibility overloads (see `Logger.kt`).
- New experimental surface should be annotated `@ExperimentalKermitApi`.
