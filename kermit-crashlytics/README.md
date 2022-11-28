# Kermit Crash Logging - Crashlytics

With the `kermit-crashlytics` module, you can setup kermit to automatically send bread crumbs and crash reports to 
Firebase Crashlytics

If you just want to write crash reports without using Kermit logging, see [CrashKiOS](https://github.com/touchlab/CrashKiOS)

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

## Dynamic Frameworks

If you are building dynamic frameworks, you'll need to configure the linker. See [the CrashKiOS docs](https://github.com/touchlab/CrashKiOS#linking).

## NSExceptionKt

CrashKiOS and Kermit previously created 2 reports on a crash because none of the crash reporting clients had an obvious way to do one. [Rick Clephas](https://github.com/rickclephas) has done some excellent work figuring that out with [NSExceptionKt](https://github.com/rickclephas/NSExceptionKt). CrashKiOS and Kermit now use parts of NSExceptionKt for crash handling.