//
//  CrashlyticsCrashHandler.swift
//  KermitSampleIOS
//
//  Created by Kevin Schildhorn on 4/14/20.
//  Copyright © 2020 Samuel Hill. All rights reserved.
//

import UIKit
import shared

class CrashKiOSLogger : KermitLogger, CrashKiOSDelegate {
    
    override init() {
        super.init()
        CrashIntegrationKt.crashInit(handler: KermitCrashHandler(del: self))
    }

    override func isLoggable(severity: KermitSeverity) -> Bool {
        return severity == KermitSeverity.assert
    }
    
    override func log(severity: KermitSeverity, message: String, tag: String?, throwable: KotlinThrowable?) {
        NSLog("(%@) %@", tag ?? "", message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    
    private func handleThrowable(throwable: KotlinThrowable?, tag:String?){
        if let realThrowable = throwable {
            NSLog(realThrowable.message ?? realThrowable.description)
        }
    }
    
    func onCrash(throwable: KotlinThrowable) {
        log(severity: KermitSeverity.assert, message: "Crash from CrashKiOS", tag: "CrashKiOS", throwable: throwable)
    }
}

class KermitCrashHandler: CrashkiosCrashHandler {

    private var delegate: CrashKiOSDelegate
    
    init(del: CrashKiOSDelegate) {
        delegate = del
    }
    
    override func crash(t: KotlinThrowable) {
        delegate.onCrash(throwable: t)
    }
}

protocol CrashKiOSDelegate {
    func onCrash(throwable: KotlinThrowable)
}
