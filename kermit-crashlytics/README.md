# Kermit Crash Logging - Crashlytics

With the `kermit-crashlytics` module, you can setup kermit to automatically send bread crumbs and crash reports to 
Firebase Crashlytics

## Dynamic Frameworks

For native/apple targets, we're using cinterop to define the underlying calls to Crashlytics. *This will only work
with static frameworks*. This is not a Kotlin or Kermit issue, but a reality of how linking works in native builds. For
a longer discussion about the issues involved, see [firebase_in_libraries](https://github.com/firebase/firebase-ios-sdk/blob/master/docs/firebase_in_libraries.md).

In your Kotlin config, that means you need to explicitly mark your framework as static:

```kotlin
ios {
    binaries {
        framework {
            isStatic = true
        }
    }
}
```

It may be possible to configure linking that will allow this to work in the future, but for now, direct Kermit integration
requires a static framework. To use dynamic frameworks, you'll need to pass in an implementation from Swift/Objc.

We'll add an example doing this in the future, but for now reach out Michael Friend or Kevin Galligan in the kotlinlang 
slack with any questions.  

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

On either  platform, you should make sure logging is configured immediately after Crashlyticis is initialized, to avoid 
a gap where some other failure may happen but logging is not capturing info.

### iOS

For iOS, besides regular logging, you will also want to configure Kotlin's uncaught exception handling. `kermit-crashlytics` 
provides the `setupCrashlyticsExceptionHook` helper function to handle this for you.

If you don't need to make kermit logging calls from Swift/Objective C code, we recommend not exporting Kermit in the 
framework exposed to your iOS app. To setup Kermit configuration you can make a top level helper method in
the `iosMain` source set that you call from Swift code to avoid binary bloat. The same rule of thumb applies
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

I you run tests in Kotlin from your native code, you'll need to provide binary implementations of the cinterop declarations.
In your app, this will be supplied by the actual Crashlytics binary, but for testing purposes, you can add the `kermit-crashlytics-test`
module. It is simply basic Objc stubs that satisfy the linker. They do not actually work, so you should not initialize your 
tests with `CrashlyticsLogWriter`.

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