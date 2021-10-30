# Kermit Swift Export Sample 
This sample demonstrates how to use Kermit from Swift/Objective C. To make kermit available in your iOS project 
you must explicitly export it to your swift framework with `export("co.touchlab:kermit:$KERMIT_VERSION")`
Keep in mind this increases your binary size because of the extra headers that need to be generated 
so use this with caution and avoid it if you can.

If you need to do more complex configuration for kermit but do not need to use kermit logging methods from 
swift, we recommend adding a kotlin helper method in the native/iosMain source set that you call from swift 
instead of exporting all of Kermit. This will only add one method to the binary of the shared framework 
but still allow you to do custom configuration for Kermit logging calls made in shared Kotlin code.
