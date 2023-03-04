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

import kotlin.test.Test
import kotlin.test.assertEquals

class LogFormatterTest {
    @Test
    fun skipEmptyTag() {
        assertEquals("Warn: Hello", DefaultFormatter.formatMessage(Severity.Warn, Tag(""), Message("Hello")))
        assertEquals("Warn: Hello", DefaultFormatter.formatMessage(Severity.Warn, null, Message("Hello")))
    }

    @Test
    fun noTag(){
        assertEquals("Warn: Hello", NoTagFormatter.formatMessage(Severity.Warn, Tag("ATag"), Message("Hello")))
        assertEquals("Hello", NoTagFormatter.formatMessage(null, Tag("ATag"), Message("Hello")))
    }

    @Test
    fun simpleFormat(){
        assertEquals("Hello", SimpleFormatter.formatMessage(Severity.Warn, Tag("ATag"), Message("Hello")))
        assertEquals("Hello", SimpleFormatter.formatMessage(null, Tag("ATag"), Message("Hello")))
        assertEquals("Hello", SimpleFormatter.formatMessage(null, Tag(""), Message("Hello")))
        assertEquals("Hello", SimpleFormatter.formatMessage(null, null, Message("Hello")))
    }

    @Test
    fun skipSeverity() {
        assertEquals("(ATag) Hello", DefaultFormatter.formatMessage(null, Tag("ATag"), Message("Hello")))
    }

    @Test
    fun basicFormat() {
        assertEquals("Warn: (ATag) Hello", DefaultFormatter.formatMessage(Severity.Warn, Tag("ATag"), Message("Hello")))
    }
}