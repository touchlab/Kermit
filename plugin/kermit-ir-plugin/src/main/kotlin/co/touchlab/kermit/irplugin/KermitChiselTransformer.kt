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

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.irUnit
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.isSubtypeOfClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class, DeprecatedForRemovalCompilerApi::class)
class KermitChiselTransformer(private val pluginContext: IrPluginContext, stripBelow: String) : IrElementTransformerVoidWithContext() {
    private val classLogger =
        pluginContext.referenceClass(ClassId(FqName("co.touchlab.kermit"), Name.identifier("Logger")))

    private val stripFunctionSet = makeStripFunctionNameSet(stripBelow)

    override fun visitCall(expression: IrCall): IrExpression {
        val logger = classLogger ?: return super.visitCall(expression)
        val recType = expression.dispatchReceiver?.type ?: expression.extensionReceiver?.type

        if (recType != null && recType.isSubtypeOfClass(logger)) {
            val functionName = expression.symbol.owner.name.identifier
            if (stripFunctionSet.contains(functionName)) {
                val scopeSymbol = currentScope?.scope?.scopeOwnerSymbol ?: expression.symbol
                return DeclarationIrBuilder(pluginContext, scopeSymbol).irUnit()
            }
        }

        return super.visitCall(expression)
    }

    private fun makeStripFunctionNameSet(severity: String): Set<String> = when (severity) {
        "None", "Verbose" -> emptySet()
        "Debug" -> setOf("v")
        "Info" -> setOf("v", "d")
        "Warn" -> setOf("v", "d", "i")
        "Error" -> setOf("v", "d", "i", "w")
        "Assert" -> setOf("v", "d", "i", "w", "e")
        "All" -> setOf("v", "d", "i", "w", "e", "a")
        else -> emptySet()
    }
}
