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

class ConsoleWriterTest {
    @Test
    fun basicLog() {
        val collector = ConsoleCollector()
        val consoleWriter = ConsoleWriter(DefaultFormatter, collector)
        consoleWriter.log(Severity.Debug, "Hello", "ATag", null)
        assertEquals(collector.logs.size, 1)
        assertEquals(collector.logs[0], "(ATag) Hello")
    }

    @Test
    fun logSeverity() {
        val collector = ConsoleCollector()
        val consoleWriter = ConsoleWriter(DefaultFormatter, collector)
        consoleWriter.log(Severity.Debug, "Hello d", "ATag", null)
        consoleWriter.log(Severity.Verbose, "Hello v", "ATag", null)
        consoleWriter.log(Severity.Info, "Hello i", "ATag", null)
        consoleWriter.log(Severity.Warn, "Hello w", "ATag", null)
        consoleWriter.log(Severity.Error, "Hello e", "ATag", null)
        consoleWriter.log(Severity.Assert, "Hello a", "ATag", null)
        assertEquals(collector.logs.size, 2)
        assertEquals(collector.errors.size, 2)
        assertEquals(collector.warnings.size, 1)
        assertEquals(collector.infos.size, 1)
    }

}

class ConsoleCollector : ConsoleIntf {
    val errors = mutableListOf<String>()
    val warnings = mutableListOf<String>()
    val infos = mutableListOf<String>()
    val logs = mutableListOf<String>()
    override fun error(output: String) {
        errors.add(output)
    }

    override fun warn(output: String) {
        warnings.add(output)
    }

    override fun info(output: String) {
        infos.add(output)
    }

    override fun log(output: String) {
        logs.add(output)
    }
}
