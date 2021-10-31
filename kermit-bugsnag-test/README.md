# kermit-bugsnag-test

This is a stub library to use for testing if you are using `kermit-bugsnag` in your build and want to run Kotlin 
tests. `kermit-bugsnag` defines the cinterop for Bugsnag, but does not provide the implementation. When you 
build your final application, your final link will include Bugsnag definitions. That works if you don't run
Kotlin/Native tests. If you do, you'll get linker errors when those tests are being built.

To avoid those errors, include `kermit-bugsnag-test` in your Kotlin/Native test dependencies.

```kotlin
sourceSets["iosTest"].dependencies {
    implementation("co.touchlab:kermit-bugsnag-test:x.y.z")
}
```

These stubs don't actually work, but they'll satisfy the linker so you can run your Kotlin build tests. When your 
framework is built into an app, you'll need to supply the Bugsnag implementation.