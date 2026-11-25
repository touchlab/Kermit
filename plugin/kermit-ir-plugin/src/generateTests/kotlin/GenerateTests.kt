package co.touchlab.kermit

import co.touchlab.kermit.irplugin.AbstractKermitBoxTest
import org.jetbrains.kotlin.compiler.plugin.devkit.generateDevKitTestsWithJUnit5

fun main() = generateDevKitTestsWithJUnit5 {
    testClass<AbstractKermitBoxTest> {
        model("box")
    }
}
