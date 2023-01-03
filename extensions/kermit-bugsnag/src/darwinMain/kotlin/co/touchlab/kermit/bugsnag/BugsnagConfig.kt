/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.bugsnag

import co.touchlab.crashkios.bugsnag.Bugsnag
import co.touchlab.crashkios.bugsnag.BugsnagConfiguration
import co.touchlab.crashkios.bugsnag.configureBugsnag
import co.touchlab.crashkios.bugsnag.setBugsnagUnhandledExceptionHook

public fun startBugsnag(config: BugsnagConfiguration) {
    configureBugsnag(config)
    Bugsnag.startWithConfiguration(config)
    setBugsnagUnhandledExceptionHook()
}

public fun configureBugsnag(config: BugsnagConfiguration) {
    configureBugsnag(config)
}

public fun setBugsnagUnhandledExceptionHook(): Unit {
    setBugsnagUnhandledExceptionHook()
}