/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample

import co.touchlab.kermit.Logger

open class SampleCommon {
    private var count = 0

    fun onClickI() {
        count++
        Logger.i { "Common click count: $count" }
    }

    fun onClickW() {
        count++
        Logger.w { "Common click count: $count" }
    }

    fun onClickE() {
        count++
        Logger.e { "Common click count: $count" }
    }

    fun onClickA() {
        count++
        Logger.a { "Common click count: $count" }
    }

    fun onClickD() {
        count++
        Logger.d { "Common click count: $count" }
    }

    fun onClickV() {
        count++
        Logger.v { "Common click count: $count" }
    }

    fun logException() {
        Logger.w(throwable = Exception("Handled")) { "Common click count: $count" }
    }
}