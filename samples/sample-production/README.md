# Kermit Basic Sample 
This sample demonstrates the most basic usage of Kermit in a multiplatform project targeting Android,
iOS, and Browser. In this basic configuration we simply add the`api("co.touchlab:kermit:${KERMIT_VERSION}")`
dependency in the `commonMain` source set in `build.gradle` for the shared module. This makes the global 
`Logger` instance available in the shared module and the kotlin modules that depend on it (`app` and `app-browser`).
This will use `Logcat` for android, console for the browser, and `println` for iOS (see [here](../../docs/IOS_CONSIDERATIONS.md) for why)

NOTE: Kermit classes and functions can not be used from swift in this sample. To make kermit available in
Swift you have to export the dependency explicitly. Exporting dependencies should be considered cautiously 
because it increases the header code that needs to be generated in and increases the binary size of the 
Kotlin framework. For an example of how to export kermit and use it from Swift, see [this sample](../sample-swift-export)

