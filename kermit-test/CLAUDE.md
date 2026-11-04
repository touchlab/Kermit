# kermit-test

Test support for code that logs with Kermit: capture log calls in memory and assert on them, instead of emitting real platform output. The entire public surface is `@ExperimentalKermitApi`.

Artifact: `co.touchlab:kermit-test`. Depends on `:kermit-core` (as `api`), `kotlin("test")`, and Stately collections.

## Key file — the whole module is one file

`src/commonMain/kotlin/co/touchlab/kermit/TestLogWriter.kt`:

- `TestConfig(minSeverity, logWriterList) : LoggerConfig` — data class for injecting test writers in place of a production config.
- `TestLogWriter(loggable: Severity) : LogWriter()` — records every log call as a nested `LogEntry(severity, message, tag, throwable)`.
  - `logs: List<LogEntry>` — snapshot accessor.
  - `assertCount(count)` — `assertEquals` on size.
  - `assertLast(check: LogEntry.() -> Boolean)` — `assertTrue` with a receiver lambda, so tests can assert on any field.
  - `reset()` — clears entries.
  - `isLoggable` compares `severity.ordinal >= loggable.ordinal`.

Storage is a Stately `frozenLinkedList` (with `@Suppress("DEPRECATION")`) for thread-safe accumulation on Native — that, not expect/actual, is how concurrency is handled here.

## Usage pattern (also used by this repo's own module tests, e.g. kermit-io)

```kotlin
val testWriter = TestLogWriter(loggable = Severity.Verbose)
val logger = Logger(TestConfig(minSeverity = Severity.Debug, logWriterList = listOf(testWriter)))
// exercise code...
testWriter.assertCount(1)
testWriter.assertLast { message == "expected" && severity == Severity.Info }
```

## Build notes

- Targets: Android, JVM, JS (nodejs), wasmJs (via `wasm-setup`), full Apple matrix, mingw, linux x64/arm64, androidNative x4 — broader than kermit-io.
- Convention plugins: `wasm-setup`, `kermit-jvm-target`, `kermit-publish`.
- The `api/` dump files are empty because everything is `@ExperimentalKermitApi` (excluded from binary-compat validation) — that's expected, not a bug.
- Unusual: `kotlin("test")` is a `commonMain` (not test) dependency, since assertions are part of the shipped API. No test source set of its own.

## Editing guidance

- This module has no expect/actual and no platform source sets — keep new code common; reach for a multiplatform library (like Stately here) before platform-specific code.
- New assertion helpers should follow the receiver-lambda style of `assertLast` and stay on `TestLogWriter`.
- Keep new API annotated `@ExperimentalKermitApi` until it's deliberately stabilized (stabilizing means it enters the binary-compat dump — a real commitment).
