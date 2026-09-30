# kermit-bugsnag

A `LogWriter` that forwards Kermit logs to Bugsnag: messages become breadcrumbs, throwables at/above a threshold are sent as handled exceptions. Native/iOS Bugsnag interop is delegated to the external **CrashKiOS** library (`co.touchlab.crashkios:bugsnag`, re-exported via `api`) — no cinterop or expect/actual here.

Artifact: `co.touchlab:kermit-bugsnag`. `@ExperimentalKermitApi`. Depends on `:kermit-core` (implementation).

## Key file

`src/commonMain/kotlin/co/touchlab/kermit/bugsnag/BugsnagLogWriter.kt` — a structural mirror of `CrashlyticsLogWriter`:

- Constructor: `(minSeverity = Severity.Info, minCrashSeverity: Severity? = Severity.Warn, messageStringFormatter = DefaultFormatter)`. `init` validates severities and calls `enableBugsnag()`.
- `log()` → `bugsnagCalls.logMessage(...)`; `sendHandledException(throwable)` when present at/above `minCrashSeverity` (`null` disables).

## Build notes

- Single `commonMain` source set. Targets: Android + full Apple matrix only (no JVM/JS/wasm).
- Convention plugins: `kermit-jvm-target`, `kermit-publish`. Empty `api/` dump is expected (experimental API is excluded from validation).
- The Bugsnag Android SDK version lives in `gradle/libs.versions.toml` (`bugsnag`); samples use the Bugsnag gradle plugin.

## Editing guidance

- **This module mirrors `extensions/kermit-crashlytics` almost line-for-line.** Apply structural changes to both, and diff against the sibling when in doubt.
- New native Bugsnag capability belongs in CrashKiOS (separate repo); this module stays a pure-common adapter.
- Keep options as defaulted constructor params on the public class; keep the surface `@ExperimentalKermitApi` unless deliberately stabilizing.
