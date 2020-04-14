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

    private var osLogObject: OSLog
    
    override init() {
        osLogObject = OSLog.default
    }
    
    init(withLogObject logObject: OSLog) {
        osLogObject = logObject
    }
    
    override func log(severity: KermitSeverity, message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.default, message)
    }
    
    override func v(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.debug, message)
    }
    override func d(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.debug, message)
    }
    override func i(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.info, message)
    }
    override func w(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.info, message)
    }
    override func e(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.error, message)
    }
    override func wtf(message: String, tag: String?, throwable: KotlinThrowable?) {
        os_log("%@", log: osLogObject, type: OSLogType.fault, message)
    }
    
}
