# Change Log

## [2.0.5] - 2024-11-20
### Changed
- Added `kermit-io` module, which contains the `RollingFileLogWriter`. This is a log writer that writes to a file with rolling logs.
- Updating `OSLogWriter` to take in three optional parameters to configure `OSLog`, and to enable/disable public logging
- Updating the WASM Implementation to match newest recommendations
- Updating kotlin version to `2.0.21`
- Updating `logThrowable` to call `throwable.stackTraceToString()` instead of `throwable.getStackTrace().joinToString("\n")`

## [2.0.4] - 2024-06-10
### Changed
- Added `ChunkedLogWriter`. This LogWriter can be used to wrap existing LogWriters and break their output into defined sizes. This is useful if your LogWriter outputs to something which limits message length (such as Logcat) (#396 thanks @psh)
```kotlin
Logger.setLogWriters(platformLogWriter().chunked(maxMessageLength = 4000))
```
## [2.0.3] - 2024-02-03

### Fixed
- Remove JUnit dependency from commonJvmMain source set (#390)

### Changed
- Kotlin 1.9.22
- [Gradle Plugin] Gradle 8.4
- [Gradle Plugin] Android Gradle Plugin 8.2.2
