---
sidebar_position: 25
---

# Testing

Kermit includes a test dependency, intended for use when testing application code that interacts with Kermit APIs but doesn't want to write to actual logs. This includes a `TestLogWriter` which holds the string outputs of log statements, and has APIs for asserting on what logs are present.

TODO: https://github.com/touchlab/Kermit/issues/332