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

import co.touchlab.kermit.irplugin.KermitComponentRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.assertEquals
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.junit.Test
import kotlin.test.Ignore

class IrPluginTest {
  @Test
  @Ignore
  fun `IR plugin success`() {
    val sourceFiles = listOf(SourceFile.kotlin(
      "Logger.kt", LoggerString
    ), SourceFile.kotlin(
      "main.kt", """
import co.touchlab.kermit.Logger
//class HeyoLogs:Logger()
fun main() {
  println("abc")
  Logger.v { "arst" }
  println("def")
//  val l = Logger()
//  l.i { "ttt" }
//  val heyo = HeyoLogs()
//  heyo.w { "www" }
}
      """.trimIndent()
    ))
    val result = compile(
      sourceFiles = sourceFiles
    )
    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
  }
}

fun compile(
    sourceFiles: List<SourceFile>,
    plugin: ComponentRegistrar = KermitComponentRegistrar(),
): KotlinCompilation.Result {
  return KotlinCompilation().apply {
    sources = sourceFiles
    useIR = true
    compilerPlugins = listOf(plugin)
    inheritClassPath = true
  }.compile()
}

fun compile(
    sourceFile: SourceFile,
    plugin: ComponentRegistrar = KermitComponentRegistrar(),
): KotlinCompilation.Result {
  return compile(listOf(sourceFile), plugin)
}
