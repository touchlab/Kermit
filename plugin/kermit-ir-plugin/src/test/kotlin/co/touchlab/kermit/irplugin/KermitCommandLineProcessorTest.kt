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

import co.touchlab.BuildConfig
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KermitCommandLineProcessorTest {
    private val processor = KermitCommandLineProcessor()

    @Test
    fun `pluginId matches BuildConfig`() {
        assertEquals(BuildConfig.KOTLIN_PLUGIN_ID, processor.pluginId)
    }

    @Test
    fun `plugin exposes stripBelow option`() {
        val option = processor.pluginOptions.find { it.optionName == "stripBelow" }
        assertNotNull(option)
        assertEquals("stripBelow", option.optionName)
    }

    @Test
    fun `processOption sets ARG_STRIP_BELOW in configuration`() {
        val configuration = CompilerConfiguration()
        val option = processor.pluginOptions.first { it.optionName == "stripBelow" }
        processor.processOption(option, "Warn", configuration)
        assertEquals("Warn", configuration.get(KermitCommandLineProcessor.ARG_STRIP_BELOW))
    }

    @Test
    fun `processOption throws on unexpected option`() {
        val configuration = CompilerConfiguration()
        val invalidOption = CliOption("unknown", "<val>", "desc")
        assertFailsWith<IllegalArgumentException> {
            processor.processOption(invalidOption, "value", configuration)
        }
    }
}
