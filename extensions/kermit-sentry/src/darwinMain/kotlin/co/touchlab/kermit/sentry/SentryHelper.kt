/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.sentry

import co.touchlab.kermit.Logger
import co.touchlab.kermit.setupUnhandledExceptionHook
import platform.Foundation.NSUUID

fun setupSentryExceptionHook(logger: Logger) {
    setupUnhandledExceptionHook(logger) {
        val crashId = generateCrashId()
        val crumb = SentryBreadcrumb()
        crumb.message = "Kotlin crash"
        crumb.data = mapOf(ktCrashKey to crashId)
        crumb.level = kSentryLevelError
        crashId
    }
}

private fun generateCrashId(): String = NSUUID().UUIDString.substring(0, 8)
private val ktCrashKey = "ktcrash"
