/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

<<<<<<< HEAD:kermit/src/commonMain/kotlin/co/touchlab/kermit/CommonWriter.kt
class CommonWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        println("$severity: ($tag) $message")
        throwable?.let {
            it.printStackTrace()
        }
    }
}
=======
internal actual fun mutableKermitConfigInit(): MutableLoggerConfig = JsMutableLoggerConfig()
>>>>>>> 86e02e7557f5a457675ffaaf7c466236d06e3959:kermit/src/jsMain/kotlin/co/touchlab/kermit/KermitConfig.kt
