---
sidebar_position: 10
---

# Crashlytics

## Setup

You first need to configure Crashlytics and CrashKiOS initialization. See [the CrashKiOS Crashlytics Tutorial Doc](https://crashkios.touchlab.co/docs/crashlytics).

## Add the dependency

```kotlin
commonMain {
    dependencies {
        implementation("co.touchlab:kermit-crashlytics:{{LATEST_GITHUB_VERSION}}")
    }
}
```

## Add the log writer

```kotlin
Logger.setLogWriters(CrashlyticsLogWriter())
```

## Custom Values

You can add custom values to Crashlytics, but not through Kermit. [Call CrashKiOS directly](https://crashkios.touchlab.co/docs/crashlytics#sending-extra-info-to-crashlytics).

```kotlin
CrashlyticsKotlin.setCustomValue("someKey", "someValue")
```