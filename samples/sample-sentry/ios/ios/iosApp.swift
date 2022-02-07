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
            options.dsn = "https://d91189f89f7d4a9daeacc42c349038c4@o1132133.ingest.sentry.io/6177444"
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
