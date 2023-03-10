---
sidebar_position: 25
---

# Testing

Kermit includes a test dependency, intended for use when testing application code that interacts 
with Kermit APIs but doesn't want to write to actual logs. This includes a `TestLogWriter` which 
holds the string outputs of log statements, and has APIs for asserting on what logs are present.

:::note
The test APIs are not yet stable, and
require [opting into](https://kotlinlang.org/docs/opt-in-requirements.html#opt-in-to-using-api)
the `@ExperimentalKermitApi` annotation. The current API is based on what we use to test Kermit internally. It may
change dramatically before we stabilize it as we consider more real-world use-cases.
:::

## Add to your dependencies

Typically you would depend on this from your test sources.

```kotlin
sourceSets {
    commonTest {
        dependencies {
            implementation("co.touchlab:kermit-test:{{LATEST_GITHUB_VERSION}}") //Add latest version
        }
    }
}
```

## Use in your tests

:::info
We strongly recommend you _inject_ logger instances into your classes rather than simply calling the
(global) static `Logger` as it will make testing easier.
:::

Suppose you have a test

```kotlin
@OptIn(ExperimentalKermitApi::class)
class MyExampleTest {
    private val testLogWriter = TestLogWriter(
        loggable = Severity.Verbose // accept everything
    )
    private val kermit = Logger(
        TestConfig(
            minSeverity = Severity.Debug,
            logWriterList = listOf(testLogWriter)
        )
    )
    
    // ...
}
```

You can either interact with the latest log entry that was produced - 

```kotlin
@Test
fun somethingInterestingHappened() {
    // ...

    testLogWriter.assertCount(1)

    // calls assertTrue() on the result of the lambda
    testLogWriter.assertLast {
        message == "the message" && severity == Severity.Info && tag == "my-tag" && throwable == null
    }
}
```

or you can interact with the list of log entries directly

```kotlin
@Test
fun somethingElseInterestingHappened() {
    // ...

    testLogWriter.assertCount(10)

    with(testLogWriter.logs[3]) {
        assertEquals("the message", message)
        assertEquals(Severity.Info, severity)
        assertEquals("my-tag", tag)
        assertNull(throwable)
    }
}
```

## Logging on Android

The _default_ setup of Kermit aims to make _production_ logging as simple as possible.  This
means that the platform logger for Android defaults to passing the log through to Android's logcat
`Log()` method.  Be sure to configure your tests accordingly, as this wont function when running 
unit tests on your local machine!

