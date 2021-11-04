# Kermit Test

The `kermit-test` module is useful for testing application code that interacts with Kermit APIs so that you can verify
that logs worked as expected and don't need to output actual log statements.

The test APIs are not yet stable, and
require [opting into](https://kotlinlang.org/docs/opt-in-requirements.html#opt-in-to-using-api)
the `@ExperimentalKermitApi` annotation. The current API is based on what we use to test Kermit internally. It may
change dramatically before we stabilize it as we consider more real-world use-cases.

## Step 1: Add to your dependencies

Typically you would depend on this from your test sources.

```kotlin
sourceSets {
    commonTest {
        dependencies {
            implementation("co.touchlab:kermit-test:x.y.z") //Add latest version
        }
    }
}
```

## Step 2: Inject into your tests

The `kermit-test` module adds a `TestConfig` and `TestLogWriter` that you can inject in place of your production
logging.

Suppose we have a component that takes a `LoggerConfig` as input and writes logs internally.

```kotlin
class Component(loggerConfig: LoggerConfig) {
    private val logger = Logger(loggerConfig)

    fun doSomething() {
        logger.i("I did it!")
    }
}
```

In your tests, you can initialize it with a `TestLogWriter` instead of your production `LoggerConfig`.

```kotlin
val testLogWriter = TestLogWriter(loggable = Severity.Verbose)
val testLogConfig = TestConfig(
    minSeverity = Severity.Debug,
    logWriterList = listOf(testLogWriter)
)

val componentUnderTest = Component(logConfig = testLogConfig)
```

## Step 3: Assert on log behavior

Now you can interact with your component and use `TestLogWriter` to assert on what was logged.

```kotlin
componentUnderTest.doSomething()

testLogWriter.assertCount(1)
testLogWriter.assertLast { message == "I did it!" }
```
