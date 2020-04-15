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
    
    override func isLoggable(severity: Severity) -> Bool {
        OSLog.default.isEnabled(type: logType(severity))
    }
    
    override func log(severity: KermitSeverity, message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: getSeverity(severity: severity), message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    
    override func v(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.info, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    override func d(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.debug, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    override func i(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.info, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    override func w(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.debug, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    override func e(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.error, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    override func wtf(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.fault, message)
        handleThrowable(throwable: throwable, tag: tag)
    }
    
    private func handleThrowable(throwable: KotlinThrowable?, tag:String?){
        if let realThrowable = throwable {
            os_log("%@", log: OSLog(subsystem: tag ?? "default", category: tag ?? "default"), type: OSLogType.fault, realThrowable.message ?? realThrowable.description)
        }
    }
}
