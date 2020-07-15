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

import kotlin.test.Test
import kotlin.test.assertTrue

class CommonLoggerTest {
    @Test
    fun commonLoggerThrowableTest() {
        var usedThrowableProvider = false
        val kermit = Kermit(
            CommonLogger(object : ThrowableStringProvider {
                override fun getThrowableString(throwable: Throwable): String {
                    usedThrowableProvider = true
                    return "Test Throwable String"
                }
            })
        )
        kermit.d(throwable = RuntimeException("My Exception")) { "Test" }

        assertTrue(usedThrowableProvider)
    }
}
