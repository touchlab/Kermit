//
//  AppDelegate.swift
//  KermitSampleIOS

// Copyright (c) 2020 Touchlab
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

import UIKit
import shared
import Firebase
import Bugsnag

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    
    static let useCrashlytics:Bool = false

//        Logger(loggerList: [
//                                OSLogLogger(),
//                                NSLogLogger()
//                                AppDelegate.useCrashlytics ? CrashlyticsLogger(minSeverity: Severity.info, minCrashSeverity: Severity.warn, printTag: true) : BugsnagLogger(minSeverity: Severity.info, minCrashSeverity: Severity.warn, printTag: true)
//    ],
//                            defaultTag: "iOSTag")
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        if(AppDelegate.useCrashlytics){
            Logger.Companion.companion().setLogWriters(logWriters: [OSLogWriter(), NSLogWriter()])
            FirebaseApp.configure()
        }else{
            Logger.Companion.companion().setLogWriters(logWriters: [OSLogWriter(), NSLogWriter(), BugsnagLogger(minSeverity: .verbose, minCrashSeverity: .warn, printTag: true)])
            Bugsnag.start(withApiKey: "PUT YOUR API KEY HERE")
        }
        
        CrashIntegrationKt.kermitCrashInit(kermit: Logger.Companion.companion(), useCrashlytics: AppDelegate.useCrashlytics)
        
        return true
    }
    
    // MARK: UISceneSession Lifecycle
    
    func application(_ application: UIApplication, configurationForConnecting connectingSceneSession: UISceneSession, options: UIScene.ConnectionOptions) -> UISceneConfiguration {
        // Called when a new scene session is being created.
        // Use this method to select a configuration to create the new scene with.
        return UISceneConfiguration(name: "Default Configuration", sessionRole: connectingSceneSession.role)
    }
    
    func application(_ application: UIApplication, didDiscardSceneSessions sceneSessions: Set<UISceneSession>) {
        // Called when the user discards a scene session.
        // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
        // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
    }
}

