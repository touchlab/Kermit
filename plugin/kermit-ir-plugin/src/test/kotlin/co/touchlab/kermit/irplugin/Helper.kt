/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.irplugin

import org.intellij.lang.annotations.Language

@Language("kotlin")
val LoggerString = """
package co.touchlab.kermit

open class Logger(
) {
    /*
     * We could reduce the methods on this object, but the native objc
     * export can't use default arguments, so we have a few extra methods defined.
     */
    inline fun v(message: () -> String) {
    }

    inline fun v(throwable: Throwable, message: () -> String) {
    }

    fun v(message: String, throwable: Throwable? = null) {
    }

    inline fun d(message: () -> String) {
    }

    inline fun d(throwable: Throwable, message: () -> String) {
    }

    fun d(message: String, throwable: Throwable? = null) {
    }

    inline fun i(message: () -> String) {
    }

    inline fun i(throwable: Throwable, message: () -> String) {
    }

    fun i(message: String, throwable: Throwable? = null) {
    }

    inline fun w(message: () -> String) {
    }

    inline fun w(throwable: Throwable, message: () -> String) {
    }

    fun w(message: String, throwable: Throwable? = null) {
    }

    inline fun e(message: () -> String) {
    }

    inline fun e(throwable: Throwable, message: () -> String) {
    }

    fun e(message: String, throwable: Throwable? = null) {
    }

    inline fun a(message: () -> String) {
    }

    inline fun a(throwable: Throwable, message: () -> String) {
    }

    fun a(message: String, throwable: Throwable? = null) {
    }

    inline fun noop(){}

    @Suppress("unused")
    companion object : Logger() {
    }
}
""".trimIndent()
