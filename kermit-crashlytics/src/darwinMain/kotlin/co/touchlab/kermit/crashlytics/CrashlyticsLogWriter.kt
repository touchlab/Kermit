/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package co.touchlab.kermit.crashlytics

import co.touchlab.crashkios.transformException
import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import kotlinx.cinterop.convert

actual class CrashlyticsLogWriter actual constructor(
    private val minSeverity: Severity,
    private val minCrashSeverity: Severity,
    private val printTag: Boolean
) : LogWriter() {
    // This was originally stored as a field on class init, but if your client app hasn't
    // initialized Crashlytics yet, you'll get a log warning, but otherwise the logger will silently
    // fail.
    private val cl: FIRCrashlytics
        get() = FIRCrashlytics.crashlytics()

    init {
        assert(minSeverity <= minCrashSeverity) {
            "minSeverity ($minSeverity) cannot be greater than minCrashSeverity ($minCrashSeverity)"
        }
    }

    override fun isLoggable(severity: Severity): Boolean = severity >= minSeverity

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        cl.log(
            if (printTag) {
                "$tag : $message"
            } else {
                message
            }
        )
        if (throwable != null && severity >= minCrashSeverity) {
            sendException(throwable)
        }
    }

    private fun sendException(throwable: Throwable) {
        transformException(throwable) { name, description, addresses ->
            val exModel = FIRExceptionModel.exceptionModelWithName(name, description)
            exModel.setStackTrace(addresses.map { FIRStackFrame.stackFrameWithAddress(it.convert()) })
            cl.recordExceptionModel(exModel)
        }
    }
}