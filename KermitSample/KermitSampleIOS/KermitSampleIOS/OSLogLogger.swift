//
//  OSLogLogger.swift
//  KermitSampleIOS
//
//  Created by Kevin Schildhorn on 4/14/20.
//  Copyright © 2020 Samuel Hill. All rights reserved.
//

import UIKit
import shared
import os.log

class OSLogLogger: KermitLogger {
    
    private func getSeverity(severity: KermitSeverity) -> OSLogType {
        switch severity {
            case KermitSeverity.verbose: return OSLogType.info
            case KermitSeverity.debug: return OSLogType.debug
            case KermitSeverity.info: return OSLogType.info
            case KermitSeverity.warn: return OSLogType.debug
            case KermitSeverity.error: return OSLogType.error
            case KermitSeverity.assert: return OSLogType.fault
            default: return OSLogType.default
        }
    }
    
    override func isLoggable(severity: KermitSeverity) -> Bool {
        OSLog.default.isEnabled(type: getSeverity(severity: severity))
    }
    
    override func log(severity: KermitSeverity, message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: getSeverity(severity: severity), message)
        if let realThrowable = throwable {
            os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: getSeverity(severity: severity), realThrowable.message ?? realThrowable.description)
        }
    }
}
