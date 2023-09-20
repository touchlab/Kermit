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

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class KermitIrGenerationExtension(
    private val messageCollector: MessageCollector,
    private val stripBelow: String
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
//    messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'string' = $string")
//    messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'file' = $file")
//    println(moduleFragment.dump())
//    moduleFragment.acceptChildrenVoid(PlayVisitor(pluginContext))
        dumpModule(moduleFragment)

        if (stripBelow != "None" && stripBelow != "Verbose") {
            moduleFragment.transform(KermitChiselTransformer(pluginContext, stripBelow), null)
        }

        dumpModule(moduleFragment)
    }

    private fun dumpModule(moduleFragment: IrModuleFragment) {
//    println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
//    println(moduleFragment.dump())
//    println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")
    }
}
