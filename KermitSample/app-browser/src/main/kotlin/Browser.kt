/*
 * Copyright (c) 2020. Touchlab, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample.js

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import co.touchlab.kermitsample.SampleCommon
import co.touchlab.kermitsample.getConsoleLogger
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlin.browser.document

val kermit = Kermit(listOf(CommonLogger(), getConsoleLogger()), "jsTag")
val common = SampleCommon(kermit)

fun main() {
    kermit.i { "loaded" }

    document.body?.append {
        div {
            button {
                text("Click Here")
                onClickFunction = {
                    common.onClick()
                }
            }
        }
    }
}
