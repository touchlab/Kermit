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

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PlatformThrowableStringProviderTest {
    @Test
    fun printsCauseTest() {
        val provider = PlatformThrowableStringProvider()

        val throwable = RuntimeException(
            "Root Exception Message",
            IllegalStateException("Cause Exception Message")
        )

        provider.getThrowableString(throwable).apply {
            assertTrue(contains("RuntimeException"))
            assertTrue(contains("Root Exception Message"))
            assertTrue(contains("IllegalStateException"))
            assertTrue(contains("Cause Exception Message"))
        }
    }

    @Test
    fun defaultImplTest() {
        val provider = object : ThrowableStringProvider {}
        val throwable = RuntimeException(
            "Root Exception Message", IllegalStateException(
                "cause 1",
                IllegalStateException(
                    "cause 2",
                    IllegalStateException(
                        "cause 3",
                        IllegalStateException(
                            "cause 4",
                            IllegalStateException("cause 5")
                        )
                    )
                )
            )
        )

        provider.getThrowableString(throwable).apply {
            assertTrue(contains("RuntimeException"))
            assertTrue(contains("Root Exception Message"))
            assertTrue(contains("IllegalStateException"))
            assertTrue(contains("cause 1"))
            assertTrue(contains("cause 2"))
            assertTrue(contains("cause 3"))

            assertFalse(contains("cause 4"))
            assertFalse(contains("cause 5"))
        }
    }
}
