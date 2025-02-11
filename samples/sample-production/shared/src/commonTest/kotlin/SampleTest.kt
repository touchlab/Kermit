/*
 * Copyright (c) 2025 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

import co.touchlab.kermit.*
import kotlin.test.Test

class SampleTest {
    @OptIn(ExperimentalKermitApi::class)
    @Test
    fun testLogWriter() {
        val logWriter = TestLogWriter(Severity.Debug)
        val logger = Logger(object : LoggerConfig {
            override val logWriterList: List<LogWriter> = listOf(logWriter)
            override val minSeverity: Severity = Severity.Info
        })

        logger.d("Something happened.")
        logger.e("An error occurred!")

        logWriter.assertCount(1)
        logWriter.assertLast { message == "An error occurred!" }
    }
}