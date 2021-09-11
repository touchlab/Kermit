package com.bnorm.template.kg

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName

class PlayVisitor: IrElementVisitorVoid {
  override fun visitElement(element: IrElement) {
    element.acceptChildrenVoid(this)
  }

  override fun visitCall(expression: IrCall) {
    super.visitCall(expression)


    val className = expression.dispatchReceiver?.type?.classFqName?.toString() as String?
    if(className?.contains("HiHo") == true){
//      val funcName = expression.symbol.signature?.
      val d = expression.symbol.owner.name
      println("HHHHHHHHHHH ${className}, $d")
    }

  }
}
