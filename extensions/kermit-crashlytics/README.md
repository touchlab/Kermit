# Kermit Crash Logging - Crashlytics

With the `kermit-crashlytics` module, you can setup kermit to automatically send bread crumbs and crash reports to 
Firebase Crashlytics

## Dynamic Frameworks

In general, using static frameworks will be easier because you don't always need to satisfy the linker with the full
Crashlytics library. You *can* use dynamic frameworks, but you'll need to link with Crashlytics, and the simplest way
to do that currently is with Cocoapods.

See our [blog post for more detail](https://touchlab.co/kermit-and-crashlytics/). We will be updating these docs as
we experiment with better ways to keep the linker happy.

## Step 1: Add Crashlytics to Your Native Project
If you already have your app setup with Crashlytics, you can skip this step, otherwise follow the steps in the Firebase 
docs to add Crashlytics crash reporting to both your [Android](https://firebase.google.com/docs/crashlytics/get-started?authuser=0&platform=android) 
and [iOS](https://firebase.google.com/docs/crashlytics/get-started?authuser=0&platform=ios) 

## Step 2: Setup Kermit Crash Logging 
First, make sure you have a dependency on `kermit` and `kermit-crashlytics` artifacts in your `commonMain` source set in 
your shared modules `build.gradle`
```kotlin
    sourceSets {
        commonMain {
            dependencies {
                implementation("co.touchlab:kermit:${KERMIT_VERSION}")
                implementation("co.touchlab:kermit-crashlytics:${KERMIT_VERSION}")
            }
        }
...
```

Second, setup the `CrashlyticsLogWriter` with your `Logger`. The constructor for both platforms is the same, so in 
shared code, or in platform-specific Kotlin, run the following:

```kotlin
Logger.addLogWriter(CrashlyticsLogWriter())
```

([Static Config](../Kermit#local-configuration) would be similar)

On either  platform, you should make sure logging is configured immediately after Crashlytics is initialized, to avoid 
a gap where some other failure may happen but logging is not capturing info.

### iOS

For iOS, besides regular logging, you will also want to configure Kotlin's uncaught exception handling. `kermit-crashlytics` 
provides the `setupCrashlyticsExceptionHook` helper function to handle this for you.

If you don't need to make kermit logging calls from Swift/Objective C code, we recommend not exporting Kermit in the 
framework exposed to your iOS app. To setup Kermit configuration you can make a top level helper method in
the `iosMain` source set that you call from Swift code. The same rule of thumb applies
to `kermit-crashlytics` and since the added api is only needed for configuration, a Kotlin helper method is
almost always the best option. Here is a basic example.

```kotlin
// in Kermit/AppInit.kt
fun setupKermit() {
    Logger.addLogWriter(CrashlyticsLogWriter())
    setupCrashlyticsExceptionHook(Logger)
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
        // Note: This MUST be the first two statement, in this order, for Kermit and Crashlytics
        // to handle any crashes in your app launch. 
        // If the app crashes before these calls run, it will not show up properly in the dashboard
        FirebaseApp.configure()
        AppInitKt.setupKermit()
        //...
    }
}
```

If providing instances built from a base `Logger` via DI, you need to make sure that the `setupCrashlytixsExceptionHook` 
call happens immediately on app launch, not lazily inside a lambda given to your DI framework. 
Example for Koin: 
```kotlin
// in iosMain
fun initKoinIos() {
    val baseLogger = Logger(StaticConfig(logWriterList = listOf(platformLogWriter(), CrashlyticsLogWriter())))
    // Note that this runs sequentially, not in the lambda pass to the module function
    setupCrashlyticsExceptionHook(log)

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
        // Note: This MUST be the first two statement, in this order, for Kermit and Crashlytics
        // to handle any crashes in your app launch. 
        // If the app crashes before these calls run, it will not show up properly in the dashboard
        FirebaseApp.configure()
        MyKoinKt.initKoinIos()
        //...
    }
}
```

# Native Test Stubs

If you are creating a static framework, and you have `CrashlyticsLogWriter` pulled into your tests, you'll need to satisfy
the native linker for your Kotlin tests to run. You can either add Crashlytics as a dependency (see [blog post](https://touchlab.co/kermit-and-crashlytics/)),
or add our test stubs. They do not function, but they satisfy the linker.

```kotlin
sourceSets {
    iosTest {
        dependencies {
            implementation("co.touchlab:kermit-crashlytics-test:${KERMIT_VERSION}")
        }
    }
}
```

# Reading iOS Crash Logs
When a crash occurs in Kotlin code, the stack trace in Crashlytics gets lost at the Swift-Kotlin barrier, 
which can make it difficult to determine the root cause of a crash that happens in Kotlin. 

![](crashlytics_crash_event_stack.png)

To remedy this, `kermit-crashlytics` reports unhandled Kotlin exceptions as separate, non-fatal exceptions, which will show up in Crashlytics with a readable stack trace. Each Kotlin crash event will have a non-fatal even with a matching unique value for the `ktcrash` key that will allow you to see the stacktrace of the exception. 
![](crashlytics_ktcrash_key.png)

Once you find the associated non fatal crash, you'll be able to see the full stack trace of the kotlin exception
![](crashlytics_nonfatal_crash.png)