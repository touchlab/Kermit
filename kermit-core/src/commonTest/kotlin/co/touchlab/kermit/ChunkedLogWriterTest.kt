/*
 * Copyright (c) 2024 Touchlab
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
import kotlin.test.assertSame

@OptIn(ExperimentalKermitApi::class)
class ChunkedLogWriterTest {
    private val testLogWriter = TestLogWriter(Severity.Warn)
    private val testObject = testLogWriter.chunked(12, 8) as ChunkedLogWriter

    @Test
    fun returnsWrappedWriter() {
        assertSame(testLogWriter, testObject.wrapped)
    }

    @Test
    fun nonChunkedOutput() {
        testObject.log(Severity.Error, "test", "my-tag")

        assertEquals(1, testLogWriter.logs.size)

        with(testLogWriter.logs[0]) {
            assertEquals(Severity.Error, severity)
            assertEquals("my-tag", tag)
            assertEquals("test", message)
            assertEquals(null, throwable)
        }
    }

    @Test
    fun twoChunksNoLinebreak() {
        testObject.log(Severity.Error, "test test test test", "my-tag")

        assertEquals(2, testLogWriter.logs.size)

        with(testLogWriter.logs[0]) {
            assertEquals(Severity.Error, severity)
            assertEquals("my-tag", tag)
            assertEquals("test test te", message)
            assertEquals(null, throwable)
        }

        with(testLogWriter.logs[1]) {
            assertEquals(Severity.Error, severity)
            assertEquals("my-tag", tag)
            assertEquals("st test", message)
            assertEquals(null, throwable)
        }
    }

    @Test
    fun twoChunksWithLinebreak() {
        testObject.log(Severity.Error, "test test\ntest test", "my-tag")

        assertEquals(2, testLogWriter.logs.size)

        with(testLogWriter.logs[0]) {
            assertEquals(Severity.Error, severity)
            assertEquals("my-tag", tag)
            assertEquals("test test", message)
            assertEquals(null, throwable)
        }

        with(testLogWriter.logs[1]) {
            assertEquals(Severity.Error, severity)
            assertEquals("my-tag", tag)
            assertEquals("test test", message)
            assertEquals(null, throwable)
        }
    }
}