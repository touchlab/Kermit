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

class SampleCommon() {
    private var count = 0

    fun logV() {
        count++
        Logger.v { "Verbose count: $count" }
    }

    fun logD() {
        count++
        Logger.d { "Debug count: $count" }
    }

    fun logI() {
        count++
        Logger.i { "Info count: $count" }
    }

    fun logW() {
        count++
        Logger.w { "Warn count: $count" }
    }

    fun logE() {
        count++
        Logger.e { "Error count: $count" }
    }

    fun logA() {
        count++
        Logger.a { "Assert count: $count" }
    }
}