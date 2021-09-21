/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package co.touchlab.kermit

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class ConcurrencyTest {

    @Test
    fun testSummationWithConcurrency() {
        val numberOfThreads = 100
        val service: ExecutorService = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(numberOfThreads)
        val testLogWriter = TestLogWriter(loggable = Severity.Verbose)
        val config = object : MutableLoggerConfig {
            override var minSeverity: Severity = Severity.Verbose
            override var logWriterList: List<LogWriter> = listOf(testLogWriter)
        }
        val logger = Logger(config)
        for (i in 0 until numberOfThreads) {
            service.execute {
                if (i % 2 == 0) {
                    config.minSeverity = Severity.Info
                } else {
                    config.minSeverity = Severity.Debug
                }
                logger.d { "message" }
                latch.countDown()
            }
        }
        latch.await()
        assertEquals(50, testLogWriter.logs.size)
    }
}