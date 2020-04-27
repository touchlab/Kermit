/*
 * Copyright (c) 2020. Touchlab, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 *
 */

package co.touchlab.kermit

interface ThrowableStringProvider {
    fun getThrowableString(throwable: Throwable) = buildString {
        fun Throwable.printCause(depth: Int = 0) {
            append("Caused by: ${toString()}\n")
            cause?.let {
                if (it !== this && depth < 2)
                    it.printCause(depth + 1)
            }
        }

        append("$throwable\n")
        throwable.cause?.printCause()
    }
}

expect class PlatformThrowableStringProvider() : ThrowableStringProvider