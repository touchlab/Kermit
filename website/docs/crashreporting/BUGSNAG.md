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