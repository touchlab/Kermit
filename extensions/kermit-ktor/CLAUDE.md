# kermit-ktor

Adapter for the Ktor HTTP client's `Logging` plugin, so Ktor client logs flow through a Kermit `Logger`. Depends on the full `:kermit` module (implementation) plus `io.ktor:ktor-client-logging`.

Artifact: `co.touchlab:kermit-ktor`. Package `co.touchlab.kermit.ktor`. Stable API — `api/android/` and `api/jvm/` dumps are validated.

## Key file (commonMain only — no expect/actual)

`src/commonMain/kotlin/co/touchlab/kermit/ktor/KermitKtorLogger.kt` — implements Ktor's `io.ktor.client.plugins.logging.Logger`:

- Primary constructor `(severity: Severity, logger: KermitLogger)` — every Ktor message logs at the one fixed `severity`.
- Secondary constructor `(severity: Severity, config: LoggerConfig, tag: String = "")` — builds the Kermit logger internally.
- Used as `install(Logging) { logger = KermitKtorLogger(Severity.Info, myKermitLogger) }` (see `README.md`).

## Build notes

- **Widest target matrix of the extension modules** (expanded in PR #477): Android, JVM, JS (browser + nodejs), wasmJs (via `wasm-setup`), full Apple matrix (incl. watchosDeviceArm64), mingwX64, linuxX64/arm64, androidNative x4. Keep parity with what Ktor client supports when adding targets.
- Convention plugins: `kermit-jvm-target`, `wasm-setup`, `kermit-publish`.

## Editing guidance

- Pure-common adapter over two stable interfaces — no expect/actual, no platform source sets; keep it that way.
- Configuration pattern: primary constructor takes a fully-configured `Logger`; the secondary constructor is convenience only. New convenience should follow that shape — sugar on top, full configuration underneath.
- Stable-API module: public changes require `./gradlew apiDump` and prefer added overloads over signature changes (the jvm `.api` dump already tracks both `<init>` overloads).
- When bumping Ktor (`gradle/libs.versions.toml`), verify the `Logger` interface hasn't changed and re-check the target matrix.
