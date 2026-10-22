# kermit-simple

Interop shim for **non-Kotlin consumers** — Objective-C/Swift (Kotlin/Native frameworks) and JavaScript. Kotlin `inline` functions (all of `Logger`'s `v/d/i/w/e/a`) are not exported to ObjC/Swift headers, and default arguments / trailing lambdas don't translate well, so this module provides non-inline, plainly-overloaded replacements plus top-level global helper functions. Re-exports `:kermit` via `api`.

Artifact: `co.touchlab:kermit-simple`.

## Key file

`src/nonKotlinMain/kotlin/co/touchlab/kermit/Logger.kt`:

- Non-inline extension functions `Logger.v/d/i/w/e/a(...)` in four overload shapes each: `(message: () -> String)`, `(throwable, message: () -> String)`, `(messageString: String)`, `(messageString, throwable)`. Each re-checks `config.minSeverity` before calling `BaseLogger.log(...)` (they can't rely on inlined gating).
- Top-level global functions `withTag`, `v/d/i/w/e/a(...)` that delegate to the `Logger` companion — so Swift can call `LoggerKt.i(...)`-style globals.

## Source-set structure (unusual — read before editing)

There is no `commonMain` code. The module defines two manual source sets, `nonKotlinMain` and `nonKotlinTest` (dependsOn `commonMain`/`commonTest`), then wires the `jsAndWasmJs` group and **every** `KotlinNativeTarget`'s default main/test source set to depend on them. Net effect: Swift/ObjC and JS consumers get this API; JVM/Android are deliberately excluded (they already have the inline API from `:kermit`). There are **no Android or JVM targets** in this module's `build.gradle.kts` (and no `android.library`/`kermit-jvm-target` plugins).

`nonKotlinTest/.../LoggerExtensionsTest.kt` is currently a stub.

## Editing guidance

- Any new public API added to `:kermit`'s `Logger` that non-Kotlin consumers need must get a non-inline mirror here — same four-overload pattern, explicit severity re-check.
- Keep functions non-inline and avoid default parameter values in ways that produce poor ObjC signatures; explicit overloads are the pattern.
- When adding source sets or targets, preserve the `nonKotlinMain` wiring — new native targets pick it up automatically via the `KotlinNativeTarget` loop in `build.gradle.kts`.
- Public API changes require `./gradlew apiDump`.
