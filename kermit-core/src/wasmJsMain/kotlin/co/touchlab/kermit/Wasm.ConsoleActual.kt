/*
 * Copyright (c) 2023 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

@JsFun("(output) => console.error(output)")
external fun consoleError(output: String)

@JsFun("(output) => console.warn(output)")
external fun consoleWarn(output: String)

@JsFun("(output) => console.info(output)")
external fun consoleInfo(output: String)

@JsFun("(output) => console.log(output)")
external fun consoleLog(output: String)

internal actual object ConsoleActual : ConsoleIntf {
    override fun error(output: String) {
        consoleError(output)
    }

    override fun warn(output: String) {
        consoleWarn(output)
    }

    override fun info(output: String) {
        consoleInfo(output)
    }

    override fun log(output: String) {
        consoleLog(output)
    }
}