/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

class ConsoleLogger : Logger() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        console.log(severity.name, tag, message)
    }

    override fun v(message: String, tag: String, throwable: Throwable?) {
        console.log(tag, message, throwable)
    }
    override fun d(message: String, tag: String, throwable: Throwable?) {
        console.log(tag, message, throwable)
    }
    override fun i(message: String, tag: String, throwable: Throwable?) {
        console.info(tag, message, throwable)
    }
    override fun w(message: String, tag: String, throwable: Throwable?) {
        console.warn(tag, message, throwable)
    }
    override fun e(message: String, tag: String, throwable: Throwable?) {
        console.error(tag, message, throwable)
    }
    override fun wtf(message: String, tag: String, throwable: Throwable?) {
        console.error(tag, message, throwable)
    }
}
