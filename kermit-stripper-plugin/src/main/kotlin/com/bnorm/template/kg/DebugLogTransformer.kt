/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.bnorm.template.kg

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression

import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isSubtypeOfClass
import org.jetbrains.kotlin.ir.util.irCall
import org.jetbrains.kotlin.name.FqName

class DebugLogTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext(){
    private val classLogger =
        pluginContext.referenceClass(FqName("co.touchlab.kermit.Logger"))!!

    private val typeAnyNullable = pluginContext.irBuiltIns.anyNType
    private val funPrintln = pluginContext.referenceFunctions(FqName("kotlin.io.println"))
        .single {
            val parameters = it.owner.valueParameters
            parameters.size == 1 && parameters[0].type == typeAnyNullable
        }

    override fun visitCall(expression: IrCall): IrExpression {
        val recType = expression.dispatchReceiver?.type
        val className = recType?.classFqName?.toString() as String?

        if (recType != null && recType.isSubtypeOfClass(classLogger)) {
            val functionName = expression.symbol.owner.name.identifier
            val stripCall = functionName == "v" || functionName == "d" || functionName == "i" || functionName == "w" || functionName == "e" || functionName == "a"
            if(stripCall){
                println("Strip Call ${className}, $functionName")
                irCall()
                return DeclarationIrBuilder(pluginContext, null).irBlockBody
            }
        }

        return super.visitCall(expression)
    }

    private fun IrBuilderWithScope.irPrintStrippedCall(
        className: String, functionName: String
    ): IrCall {
        val concat = irConcat()
        concat.addArgument(irString("Stripped ${className}.${functionName}"))
        return irCall(funPrintln).also { call ->
            call.putValueArgument(0, concat)
        }
    }
}