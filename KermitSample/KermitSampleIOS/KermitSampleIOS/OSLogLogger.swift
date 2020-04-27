//
//  OSLogLogger.swift
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
