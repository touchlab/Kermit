//
//  CrashlyticsCrashHandler.swift
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

class KermitCrashHandler: CrashkiosCrashHandler {
    
    let kermit = KermitKermit(logger: UtilKt.getNSLogger())

    override init() {
        self.kermit.v(withTag: "CrashKiOS", message: { "Crash handler initialized" })
    }

    override func crash(t: KotlinThrowable) {
        self.kermit.wtf(tag: "CrashKiOS", throwable: t, message: { "Crash!" })
    }
}
