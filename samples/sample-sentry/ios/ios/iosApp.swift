//
//  iosApp.swift
//  ios
//
//  Created by Kevin Galligan on 12/29/21.
//

import SwiftUI
import Sentry

@main
struct iosApp: App {
    init() {
        SentrySDK.start { options in
            options.dsn = "https://162e7a75c8804edbb49e2d038dc9e1c5@o326303.ingest.sentry.io/6201254"
            options.debug = true // Enabled debug when first installing is always helpful

            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.tracesSampleRate = 1.0
        }
    }

    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
