# kermit-io

File-based logging for Kermit: publishes `RollingFileLogWriter`, a `LogWriter` that writes formatted log lines to a size-rolling set of files, built on kotlinx-io's multiplatform filesystem (`SystemFileSystem`).

Artifact: `co.touchlab:kermit-io`. Package `co.touchlab.kermit.io`. Depends on `:kermit-core` (implementation), `kotlinx-datetime` and `kotlinx-io` (both `api`), coroutines.

## Key files (all in commonMain — this module is pure common code)

- `RollingFileLogWriter.kt` — `open class RollingFileLogWriter : LogWriter()`. Primary constructor `(config, clock, messageStringFormatter = DefaultFormatter, fileSystem = SystemFileSystem)`; secondary omits `clock` (defaults `Clock.System`). Everything is injectable for tests.
- `RollingFileLogWriterConfig.kt` — `data class` with `logFileName`, `logFilePath: Path`, `rollOnSize` (default 10MB), `maxLogFiles` (default 5), `logTag` (default true), `prependTimestamp` (default true).

## How it works (know this before editing)

- **Async single-writer**: a dedicated `newSingleThreadContext("RollingFileLogWriter")` coroutine consumes an unbuffered `Channel<Buffer>`; `log()` pushes via `trySendBlocking`, so callers block until the write completes. Don't change channel buffering without thinking through ordering and backpressure.
- **Formatting**: delegates to `MessageStringFormatter`; optional ISO-8601 timestamp prefix from the injected `Clock`; appends `throwable.stackTraceToString()`.
- **Rolling**: index 0 is `<name>.log`, older files `<name>-N.log`. Rolls when `max(trackedSize, fileMetadataSize) > rollOnSize`; deletes the oldest, then `atomicMove`s each file up one index from highest to lowest. File size is tracked internally rather than trusting filesystem metadata (stale on Windows while a write handle is open). The sink is closed before rolling and reopened (append) after.
- **IO error resilience** (commit 42ff100): write-path `IOException`s are caught — motivated by iOS `NSFileProtectionComplete` failing writes while the device is locked. On error it sets an `ioErrorActive` flag (avoids log spam), closes/nulls the sink, resets tracked size from disk, and keeps the writer coroutine alive so logging resumes when file access returns ("Log file access restored"). The KDoc documents the iOS behavior and the Swift `.protectionKey` workaround. Preserve this recover-don't-crash behavior in any refactor.

## Build notes

- Targets: Android, JVM, full Apple matrix, mingwX64, linuxX64/arm64, androidNative x4. **No JS/wasm** (kotlinx-io filesystem constraint) — the `wasm-setup` plugin is deliberately absent.
- Convention plugins: `kermit-jvm-target`, `kermit-publish`. A `commonJvm` source-set group exists but only for test dependencies (junit, Robolectric via `androidUnitTest`).

## Editing guidance

- This module has **zero** expect/actual declarations — platform variation is absorbed by kotlinx-io's `FileSystem` abstraction, injected via constructor (along with `Clock` and the formatter). Keep it that way: new platform behavior should be injected through the constructor, not added as platform source sets.
- Tests (`commonTest/RollingFileLogWriterTest.kt`) use real temp dirs under `build/tmp` and `delay(200)` to wait for the async writer — new behavior tests should follow suit (small `rollOnSize` values make rolling testable). `commonTest` depends on `:kermit-test`.
- Public API changes require `./gradlew apiDump` (dumps in `api/jvm/`, `api/android/`).
