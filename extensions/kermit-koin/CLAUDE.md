# kermit-koin

Two-way bridge between Kermit and Koin: route Koin's internal diagnostics through Kermit, and inject tagged Kermit `Logger` instances via Koin. Depends on the full `:kermit` module (implementation) plus `koin-core`.

Artifact: `co.touchlab:kermit-koin`. Package `co.touchlab.kermit.koin`. Stable API (not experimental) — `api/android/` and `api/jvm/` dumps are validated.

## Key files (commonMain only — no expect/actual)

- `KermitKoinLogger.kt` — extends Koin's `org.koin.core.logger.Logger`; maps Koin `Level` DEBUG/INFO/WARNING/ERROR (NONE is dropped) to `Logger.d/i/w/e`. Passed to `startKoin { logger(KermitKoinLogger(Logger.withTag("koin"))) }`.
- `GetLoggerWithTag.kt` — `kermitLoggerModule(baseLogger: Logger)`: a Koin module exposing a `factory` that yields `baseLogger.withTag(tag)` (or `baseLogger` when no tag param); and `inline fun <reified L : Logger> Scope.getLoggerWithTag(tag: String)` which resolves it via `parametersOf(tag)`.

## Build notes

- Targets: Android, JVM, JS (browser + nodejs), wasmJs (via `wasm-setup`), macOS/iOS, watchOS (Arm64/SimulatorArm64/X64), tvOS, linuxX64, mingwX64.
- **Deliberately excluded targets** (Koin doesn't support them — a comment in `build.gradle.kts` lists them): `watchosArm32`, `watchosDeviceArm64`, all `androidNative*`, `linuxArm64`. Don't add targets without checking Koin support first.
- Convention plugins: `wasm-setup`, `kermit-jvm-target`, `kermit-publish`.

## Editing guidance

- Pure-common adapter — keep it that way. No expect/actual; platform variation is Koin's problem, not this module's.
- Configuration pattern: the consumer constructs and passes their own configured `Logger` — don't bake in writer or severity defaults here beyond what `Logger` provides.
- This is a stable-API module: public changes require `./gradlew apiDump` and a compatibility decision (prefer overloads/deprecation over signature changes).
- When bumping the Koin version (`gradle/libs.versions.toml`), re-check the target-support list above.
