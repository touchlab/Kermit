---
sidebar_position: 20
---

# Configuration

## [Logger Setup](LOGGER_SETUP)

Logger setup includes various topics, including static vs mutable, and adding non-default `LogWriter` instances.

## [Message Formatting](MESSAGE_FORMATTING)

`LogWriter` instances sometimes need to include tag or severity in the log message string. You may also want to add some custom info to the message strings. `MessageStringFormatter` allows for central formatting configuration.

## [Non-Kotlin Environments](NON_KOTLIN)

Kermit's API is designed to be Kotlin-friendly. That includes methods with default parameters. To call Kermit from other environments, for example Swift, you'll probably want to add and export `kermit-simple`.