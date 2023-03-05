# Bugsnag

## Setup

You first need to configure Bugsnag and CrashKiOS initialization. See [the CrashKiOS Bugsnag Tutorial Doc](https://crashkios.touchlab.co/docs/bugsnag).

## Add the dependency

```kotlin
commonMain {
    dependencies {
        implementation("co.touchlab:kermit-bugsnag:{{LATEST_GITHUB_VERSION}}")
    }
}
```

## Add the log writer

```kotlin
Logger.setLogWriters(BugsnagLogWriter())
```

## Custom Values

You can add custom values to Bugsnag, but not through Kermit. [Call CrashKiOS directly](https://crashkios.touchlab.co/docs/bugsnag#sending-extra-info-to-bugsnag).

```kotlin
BugsnagKotlin.setCustomValue("someKey", "someValue")
```