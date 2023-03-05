---
sidebar_position: 26
---

# iOS Logging

There are three `LogWriter` implementations for iOS.

## XcodeSeverityWriter

This is the default `LogWriter`. It is designed for local development. Each severity is represented with an emoji. `Throwable` instances sent to this writer will be written with `println` rather than oslog because oslog trims long strings.

```shell
2023-03-05 08:48:03.864138-0500 KermitSampleIOS[58575:7607179] 🟢 Try a log
2023-03-05 08:48:04.622452-0500 KermitSampleIOS[58575:7607442] [Bugsnag] [INFO] Sent session 7D3188F7-2B37-4189-9F92-63BC7172D01B
2023-03-05 08:48:09.999884-0500 KermitSampleIOS[58575:7607179] 🟢 Common click count: 1
2023-03-05 08:48:11.333941-0500 KermitSampleIOS[58575:7607179] 🔵 Common click count: 2
2023-03-05 08:48:13.104265-0500 KermitSampleIOS[58575:7607179] 🟡 Common click count: 3
2023-03-05 08:48:13.568351-0500 KermitSampleIOS[58575:7607179] 🔴 Common click count: 4
```

## OSLogWriter

This is the parent class of `XcodeSeverityWriter`. There is no emoji added for severity, and `Throwable` is sent as a string to oslog. This may trim exceptions. You can implement a custom version that writes each line of a stack trace to oslog, or whatever else you'd like to do. Override `logThrowable`.

## NSLogWriter

Legacy implementation using `NSLog`.