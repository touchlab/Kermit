# kermit-crashlytics

A `LogWriter` that forwards Kermit logs to Firebase Crashlytics: messages become breadcrumbs, and throwables at/above a threshold are sent as non-fatal handled exceptions. All native/iOS Crashlytics interop is delegated to the external **CrashKiOS** library (`co.touchlab.crashkios:crashlytics`, re-exported via `api`) — there is no cinterop or expect/actual in this module.

Artifact: `co.touchlab:kermit-crashlytics`. `@ExperimentalKermitApi`. Depends on `:kermit-core` (implementation).

## Key file

`src/commonMain/kotlin/co/touchlab/kermit/crashlytics/CrashlyticsLogWriter.kt`:

- Constructor: `(minSeverity = Severity.Info, minCrashSeverity: Severity? = Severity.Warn, messageStringFormatter = DefaultFormatter)`. `init` validates that `minCrashSeverity >= minSeverity` and calls `enableCrashlytics()`.
- `log()` → `crashlyticsCalls.logMessage(...)` always (when loggable); `crashlyticsCalls.sendHandledException(throwable)` when a throwable is present at/above `minCrashSeverity`. `minCrashSeverity = null` disables handled-exception reporting.
- `setCrashlyticsUnhandledExceptionHook` (for iOS crash reporting setup) comes from CrashKiOS, not this repo — it's visible transitively via the `api` dependency and documented in this module's `README.md`.

## Build notes

- Single `commonMain` source set. Targets: Android + full Apple matrix only (**no JVM, no JS/wasm** — Crashlytics doesn't exist there).
- Convention plugins: `kermit-jvm-target`, `kermit-publish`. `api/kermit-crashlytics.api` is empty because the API is experimental — expected.

## Editing guidance

- **This module mirrors `extensions/kermit-bugsnag` almost line-for-line.** Structural changes (constructor params, severity semantics, validation) should usually be applied to both — check the other module before and after editing.
- Anything requiring new native Crashlytics calls belongs in CrashKiOS (separate repo), not here — this module stays a pure-common adapter.
- Keep new options as defaulted constructor params on the public class.
- Keep `@ExperimentalKermitApi` on the public surface unless deliberately stabilizing (which adds it to binary-compat dumps).
