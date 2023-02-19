# Kermit Crash Logging - Bugsnag

With the `kermit-bugsnag` module, you can setup kermit to automatically send bread crumbs and crash reports to Bugsnag.

If you just want to write crash reports without using Kermit logging, see [CrashKiOS](https://github.com/touchlab/CrashKiOS)

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

Crash logging on iOS requires a few extra steps. Kermit provides wrapper functions that call into CrashKiOS underneath. You can read more about those methods in the [CrashKiOS README](https://github.com/touchlab/CrashKiOS). In our example here, we'll use `startBugsnag()` wrapper function.

If you don't need to make kermit logging calls from Swift/Objective C code, we recommend not exporting Kermit in the 
framework exposed to your iOS app. To setup Kermit configuration you can make a top level helper method in the `iosMain` 
sourceset that you call from Swift code to avoid binary bloat. The same rule of thumb applies to `kermit-bugsnag` and 
since the added api is only needed for configuration, a Kotlin helper method is almost always the best option. Here is a basic example.

```kotlin
// in Kermit/AppInit.kt
fun startKermit(config: BugsnagConfiguration) {
    startBugsnag(config)
    Logger.addLogWriter(BugsnagLogWriter())  
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
        let config = BugsnagConfiguration.loadConfig()
        AppInitKt.startKermit(config: config)
        //...
    }
}
```

## Testing

If you're building a dynamic framework, or you're using Kotlin version 1.8.0+ which builds dynamic by default,
building for tests will give you an error like this:
```shell
Undefined symbols for architecture x86_64:
  "_OBJC_CLASS_$_BugsnagFeatureFlag", referenced from:
      objc-class-ref in libco.touchlab.crashkios:bugsnag-cache.a(result.o)
  "_OBJC_CLASS_$_BugsnagStackframe", referenced from:
      objc-class-ref in libco.touchlab.crashkios:bugsnag-cache.a(result.o)
  "_OBJC_CLASS_$_BugsnagError", referenced from:
      objc-class-ref in libco.touchlab.crashkios:bugsnag-cache.a(result.o)
  "_OBJC_CLASS_$_Bugsnag", referenced from:
      objc-class-ref in libco.touchlab.crashkios:bugsnag-cache.a(result.o)
      objc-class-ref in libco.touchlab:kermit-bugsnag-cache.a(result.o)
ld: symbol(s) not found for architecture x86_64
```

To resolve this, you should tell the linker that Bugsnag will be added later. You can do that directly, or you can use our Gradle plugin. It will find all Xcode Frameworks being built by Kotlin and add the necessary linker arguments.

```kotlin
plugins {
  id("co.touchlab.crashkios.bugsnaglink") version "x.y.z"
}
```

## NSExceptionKt

CrashKiOS and Kermit previously created 2 reports on a crash because none of the crash reporting clients had an obvious way to do one. [Rick Clephas](https://github.com/rickclephas) has done some excellent work figuring that out with [NSExceptionKt](https://github.com/rickclephas/NSExceptionKt). CrashKiOS and Kermit now use parts of NSExceptionKt for crash handling.