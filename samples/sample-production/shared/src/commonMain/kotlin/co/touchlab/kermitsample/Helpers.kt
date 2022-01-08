/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.crashlytics.CrashlyticsLogWriter
import co.touchlab.kermit.platformLogWriter

internal fun configApp(production: Boolean){
    val config = if (production) {
        StaticConfig(
            minSeverity = Severity.Warn,
            logWriterList = listOf(CrashlyticsLogWriter())
        )
    } else {
        StaticConfig(
            minSeverity = Severity.Verbose,
            logWriterList = listOf(platformLogWriter())
        )
    }
    val logger = Logger(config)
    NotDI.logger = logger
}