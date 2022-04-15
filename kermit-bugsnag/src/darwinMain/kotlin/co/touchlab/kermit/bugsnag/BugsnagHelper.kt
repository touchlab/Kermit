/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.bugsnag

import co.touchlab.crashkios.transformException
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.setupUnhandledExceptionHook
import platform.Foundation.NSUUID

@ExperimentalKermitApi
fun setupBugsnagExceptionHook(logger: Logger) {
    setupUnhandledExceptionHook(logger) {
        val crashId = generateCrashId()
        Bugsnag.leaveBreadcrumbWithMessage(
            "Kotlin Crash",
            mapOf(Pair(ktCrashKey, crashId)),
            BSGBreadcrumbType.BSGBreadcrumbTypeError
        )

        // Does the transformation actually run?  Will a new exception
        // break out of the current breadcrumb trail?
        transformException( Exception("Brand-new Wrapper Exception", it) ) { name, description, addresses ->
            Bugsnag.leaveBreadcrumbWithMessage(
                "Kotlin Crash - inside transformException",
                mapOf(
                    Pair(ktCrashKey, crashId),
                    Pair("Name", name),
                    Pair("Desc", description),
                    Pair("Addr list", addresses.toString())
                ),
                BSGBreadcrumbType.BSGBreadcrumbTypeError
            )

            Bugsnag.notify(BugsnagNSException(addresses, name, description))
        }
        crashId
    }
}

private fun generateCrashId():String = NSUUID().UUIDString.substring(0, 8)
private val ktCrashKey = "ktcrash"
