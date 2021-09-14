package com.bnorm.template.kg

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.isSubtypeOfClass
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName

class PlayVisitor(pluginContext: IrPluginContext) : IrElementVisitorVoid {
    private val classLogger =
        pluginContext.referenceClass(FqName("co.touchlab.kermit.Logger"))!!

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitCall(expression: IrCall) {
        super.visitCall(expression)

        val recType = expression.dispatchReceiver?.type
        val className = recType?.classFqName?.toString() as String?

        if (recType != null && recType.isSubtypeOfClass(classLogger)) {
            val functionName = expression.symbol.owner.name.identifier
            val stripCall = functionName == "v" || functionName == "d" || functionName == "i" || functionName == "w" || functionName == "e" || functionName == "a"
            if(stripCall){
                println("Strip Call ${className}, $functionName")

            }
        }

    }
}
