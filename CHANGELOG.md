# Change Log

## [2.0.4] - 2024-06-10
### Changed
- Added `ChunkedLogWriter`. This LogWriter can be used to wrap existing LogWriters and break their output into defined sizes. This is useful if your LogWriter outputs to something which limits message length (such as Logcat)
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
