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
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
class KermitCompilerPluginRegistrar(private val defaultStripBelow: String) : CompilerPluginRegistrar() {

    @Suppress("unused") // Used by service loader
    constructor() : this(
        defaultStripBelow = "None",
    )

    override val supportsK2: Boolean = true

    override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val stripBelow = configuration.get(KermitCommandLineProcessor.ARG_STRIP_BELOW, defaultStripBelow)

        IrGenerationExtension.registerExtension(
            KermitIrGenerationExtension(messageCollector, stripBelow),
        )
    }
}
