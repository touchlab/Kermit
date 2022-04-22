# Kermit Crash Logging - Bugsnag

With the `kermit-bugsnag` module, you can setup kermit to automatically send bread crumbs and crash reports to Bugsnag.

## Dynamic Frameworks

Similar to Crashlytics, direct integration to Bugsnag on Apple targets requires building static Kotlin frameworks. See
[Crashlytics Docs](../kermit-crashlytics/README.md) for more information.

## Step 1: Add Bugsnag to Your Project
If you already have your app setup with bugsnag, you can skip this step, otherwise follow the steps in the Bugsnag docs 
to add Bugsnag crash reporting to both your [Android](https://docs.bugsnag.com/platforms/android/) and [iOS](https://docs.bugsnag.com/platforms/ios/) apps.

## Step 2: Setup Kermit Crashlogging 
First, make sure you have a dependency on `kermit` and `kermit-bugsnag` artifacts in your `commonMain` source set in 
your shared modules `build.gradle`
```kotlin
    sourceSets {
        commonMain {
            dependencies {
                implementation("co.touchlab:kermit:${KERMIT_VERSION}")
                implementation("co.touchlab:kermit-bugsnag:${KERMIT_VERSION}")
            }
        }
...
```

Second, setup the `BugsnagLogWriter` with your `Logger`. The constructor for both platforms is the same, so in
shared code, or in platform-specific Kotlin, run the following:

```kotlin
Logger.addLogWriter(BugsnagLogWriter())
```

([Static Config](../Kermit#local-configuration) would be similar)

On either platform, you should make sure logging is configured immediately after Bugsnag is initialized, to avoid
a gap where some other failure may happen but logging is not capturing info.

### iOS

For iOS, besides regular logging, you will also want to configure Kotlin's uncaught exception handling. `kermit-bugsnag`
provides the `setupBugsnagExceptionHook` helper function to handle this for you.

If you don't need to make kermit logging calls from Swift/Objective C code, we recommend not exporting Kermit in the 
framework exposed to your iOS app. To setup Kermit configuration you can make a top level helper method in the `iosMain` 
sourceset that you call from Swift code to avoid binary bloat. The same rule of thumb applies to `kermit-bugsnag` and 
since the added api is only needed for configuration, a Kotlin helper method is almost always the best option. Here is a basic example.

```kotlin
// in Kermit/AppInit.kt
fun setupKermit() {
    Logger.addLogWriter(BugsnagLogWriter())
    setupBugsnagExceptionHook(Logger)
}
```

```swift
// in AppDelegate.swift
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    ...
    func application(
        _ application: UIApplication, 
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Note: This MUST be the first two statement, in this order, for Kermit and Bugsnag
        // to handle any crashes in your app launch. 
        // If the app crashes before these calls run, it will not show up properly in the dashboard
        Bugsnag.start(withApiKey: "YOUR API KEY HERE")
        AppInitKt.setupKermit()
        //...
    }
}
```

If providing instances built from a base `Logger` via DI, you need to make sure that the `setupBugsnagExceptionHook` 
call happens immediately on app launch, not lazily inside a lambda given to your DI framework. 
Example for Koin: 
```kotlin
// in iosMain
fun initKoinIos() {
    val baseLogger = Logger(StaticConfig(logWriterList = listOf(platformLogWriter(), BugsnagLogWriter())))
    // Note that this runs sequentially, not in the lambda pass to the module function
    setupBugsnagExceptionHook(log)

    return initKoin(
        module { 
            factory { (tag: String?) -> if (tag != null) baseLogger.withTag(tag) else baseLogger }
        }
    )
}
```

```swift
// in AppDelegate.swift
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    ...
    func application(
        _ application: UIApplication, 
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Note: This MUST be the first two statement, in this order, for Kermit and Bugsnag
        // to handle any crashes in your app launch. 
        // If the app crashes before these calls run, it will not show up properly in the dashboard
        FirebaseApp.configure()
        MyKoinKt.initKoinIos()
        //...
    }
}
```

# Native Test Stubs

I you run tests in Kotlin from your native code, you'll need to provide binary implementations of the cinterop declarations.
In your app, this will be supplied by the actual Bugsnag binary, but for testing purposes, you can add the `kermit-bugsnag-test`
module. It is simply basic Objc stubs that satisfy the linker. They do not actually work, so you should not initialize your
tests with `BugsnagLogWriter`.

```kotlin
sourceSets {
    iosTest {
        dependencies {
            implementation("co.touchlab:kermit-bugsnag-test:${KERMIT_VERSION}")
        }
    }
}
```

# Reading iOS Crash Logs
When a crash occurs in Kotlin code, the stack trace in Bugsnag gets lost at the Swift-Kotlin barrier, which can make it
difficult to determine the root cause of a crash that happens in Kotlin. 

// BUGSNAG CRASH EVENT IMAGE 

To remedy this, `kermit-bugsnag` reports unhandled Kotlin exceptions as separate, non-fatal exceptions, which will show up in Bugsnag with a readable stack trace. Each Kotlin crash event will have a non-fatal even with a matching unique value for the `ktcrash` key that will allow you to see the stacktrace of the exception. 

// BUGSNAG NON FATAL EVENT IMAGE 
