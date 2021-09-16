/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample.js

import co.touchlab.kermit.Logger
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.bugsnag.Bugsnag
import co.touchlab.kermit.bugsnag.BugsnagLogger
import co.touchlab.kermit.platformLogWriter
import co.touchlab.kermitsample.CrashBot
import co.touchlab.kermitsample.SampleCommon
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlin.browser.document


fun main() {
    Logger.setLogWriters(platformLogWriter(), BugsnagLogger())
    Bugsnag.start("54981b8cd6a9df574efddcef0cd0f7cc")
    val common = SampleCommon()
    val crashBot = CrashBot()
    Logger.i { "loaded" }

    document.body?.append {
        div {
            button {
                text("Click Here")
                onClickFunction = {
                    common.onClick()
                }
            }
            button {
                text("log exception")
                onClickFunction = {
                    common.logException()
                }
            }
            button {
                text("crash")
                onClickFunction = {
                    crashBot.goCrash()
                }
            }
        }
    }
}
