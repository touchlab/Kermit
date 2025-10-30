# Ktor Integration

Kermit's Ktor integration allows you to pass in a Kermit logger instance to your Ktor client.

## Setup

### Add the dependency

```kotlin
commonMain {
    dependencies {
        implementation("co.touchlab:kermit-ktor:{{LATEST_GITHUB_VERSION}}")
    }
}
```

### Add the logger to your client

```kotlin
val httpClient = HttpClient {
    install(Logging) {
        logger = KermitKtorLogger(severity = Severity.Info, logger = KermitLogger)
        level = LogLevel.INFO
    }
}
```

or alternatively

```kotlin
val httpClient = HttpClient {
    install(Logging) {
        logger = KermitKtorLogger(severity = Severity.Info, config = loggerConfigInit(CommonWriter()), tag = "")
        level = LogLevel.INFO
    }
}
```

This takes in a Kermit logger instance, or alternatively a Kermit logger configuration to create a logger from.
This should allow some flexibility in how you want to set up your logging. You will also need to pass in a severity
level to log to, since the Ktor logger interface does not have levels itself.