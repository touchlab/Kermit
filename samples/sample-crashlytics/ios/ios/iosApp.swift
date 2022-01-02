//
//  iosApp.swift
//  ios
//
//  Created by Kevin Galligan on 12/29/21.
//

import SwiftUI

@main
struct iosApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
