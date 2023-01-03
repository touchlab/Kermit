/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.crashlytics

import co.touchlab.crashkios.crashlytics.CrashlyticsCalls
import co.touchlab.crashkios.crashlytics.CrashlyticsCallsActual
import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity

@ExperimentalKermitApi
class CrashlyticsLogWriter(
    private val minSeverity: Severity = Severity.Info,
    private val minCrashSeverity: Severity = Severity.Warn,
    private val printTag: Boolean = true
) : LogWriter() {

    private val crashlyticsCalls: CrashlyticsCalls = CrashlyticsCallsActual()

    init {
        if(minSeverity > minCrashSeverity) {
            throw IllegalArgumentException("minSeverity ($minSeverity) cannot be greater than minCrashSeverity ($minCrashSeverity)")
        }

        enableCrashlytics()
    }

    override fun isLoggable(severity: Severity): Boolean = severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        crashlyticsCalls.logMessage(
            if (printTag) {
                "$tag : $message"
            } else {
                message
            }
        )
        if (throwable != null && severity >= minCrashSeverity) {
            crashlyticsCalls.sendHandledException(throwable)
        }
    }
}
