/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsamplecrashlog

import android.app.Application
import co.touchlab.kermit.ExperimentalKermitApi
import co.touchlab.kermit.Logger
import co.touchlab.kermit.bugsnag.BugsnagLogWriter
import co.touchlab.kermit.platformLogWriter
import com.bugsnag.android.Bugsnag

class SampleApp : Application() {

    @OptIn(ExperimentalKermitApi::class)
    override fun onCreate() {
        super.onCreate()
        // Setup crash crash reporting service and static log writer on app creation
        Bugsnag.start(this, "ADD YOUR API KEY HERE")
        Logger.setLogWriters(platformLogWriter(), BugsnagLogWriter())
    }
}
