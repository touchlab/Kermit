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

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isSubtypeOfClass
import org.jetbrains.kotlin.name.FqName

class KermitChiselTransformer(
    private val pluginContext: IrPluginContext,
    stripBelow: String
) : IrElementTransformerVoidWithContext() {
    private val classLogger =
        pluginContext.referenceClass(FqName("co.touchlab.kermit.Logger"))!!

    private val stripFunctionSet = makeStripFunctionNameSet(stripBelow)

    override fun visitCall(expression: IrCall): IrExpression {
        val recType = expression.dispatchReceiver?.type

        if (recType != null && recType.isSubtypeOfClass(classLogger)) {
            val functionName = expression.symbol.owner.name.identifier
            val stripCall = stripFunctionSet.contains(functionName)
            if (stripCall) {
                return DeclarationIrBuilder(pluginContext, expression.symbol).irUnit()
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