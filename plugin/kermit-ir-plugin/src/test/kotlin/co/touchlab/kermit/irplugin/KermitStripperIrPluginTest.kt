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

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import kotlin.test.assertEquals
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@OptIn(ExperimentalCompilerApi::class)
class IrPluginTest {
    @Test
    fun `compilation succeeds with no stripping`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("Logger.kt", LoggerString),
                SourceFile.kotlin(
                    "main.kt",
                    """
                    import co.touchlab.kermit.Logger
                    fun main() {
                      Logger.v { "verbose" }
                      Logger.d { "debug" }
                      Logger.i { "info" }
                      Logger.w { "warn" }
                      Logger.e { "error" }
                      Logger.a { "assert" }
                    }
                    """.trimIndent(),
                ),
            ),
            stripBelow = "None",
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `compilation succeeds with strip below Error`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("Logger.kt", LoggerString),
                SourceFile.kotlin(
                    "main.kt",
                    """
                    import co.touchlab.kermit.Logger
                    fun main() {
                      Logger.v { "verbose" }
                      Logger.d { "debug" }
                      Logger.i { "info" }
                      Logger.w { "warn" }
                      Logger.e { "error" }
                      Logger.a { "assert" }
                    }
                    """.trimIndent(),
                ),
            ),
            stripBelow = "Error",
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `compilation succeeds with strip below All`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("Logger.kt", LoggerString),
                SourceFile.kotlin(
                    "main.kt",
                    """
                    import co.touchlab.kermit.Logger
                    fun main() {
                      Logger.v { "verbose" }
                      Logger.d { "debug" }
                      Logger.i { "info" }
                      Logger.w { "warn" }
                      Logger.e { "error" }
                      Logger.a { "assert" }
                    }
                    """.trimIndent(),
                ),
            ),
            stripBelow = "All",
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `compilation succeeds with instance logger`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("Logger.kt", LoggerString),
                SourceFile.kotlin(
                    "main.kt",
                    """
                    import co.touchlab.kermit.Logger
                    fun main() {
                      val logger = Logger()
                      logger.v { "verbose" }
                      logger.d { "debug" }
                      logger.i { "info" }
                    }
                    """.trimIndent(),
                ),
            ),
            stripBelow = "Warn",
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `verify log calls below threshold are stripped at runtime`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("Logger.kt", LoggerString),
                SourceFile.kotlin(
                    "Main.kt",
                    """
                    import co.touchlab.kermit.Logger
                    object Tracker {
                        var verboseCount = 0
                        var debugCount = 0
                        var infoCount = 0
                        var errorCount = 0
                    }
                    fun runLogs() {
                        Logger.v { Tracker.verboseCount++; "v" }
                        Logger.d { Tracker.debugCount++; "d" }
                        Logger.i { Tracker.infoCount++; "i" }
                        Logger.e { Tracker.errorCount++; "e" }
                    }
                    """.trimIndent(),
                ),
            ),
            stripBelow = "Info",
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kClass = result.classLoader.loadClass("MainKt")
        kClass.getMethod("runLogs").invoke(null)

        val trackerClass = result.classLoader.loadClass("Tracker")
        val instance = trackerClass.getField("INSTANCE").get(null)
        val verboseCount = trackerClass.getMethod("getVerboseCount").invoke(instance)
        val debugCount = trackerClass.getMethod("getDebugCount").invoke(instance)
        val infoCount = trackerClass.getMethod("getInfoCount").invoke(instance)
        val errorCount = trackerClass.getMethod("getErrorCount").invoke(instance)

        assertEquals(0, verboseCount)
        assertEquals(0, debugCount)
        assertEquals(1, infoCount)
        assertEquals(1, errorCount)
    }
}

@OptIn(ExperimentalCompilerApi::class)
fun compile(sourceFiles: List<SourceFile>, stripBelow: String = "None"): JvmCompilationResult = KotlinCompilation().apply {
    sources = sourceFiles
    compilerPluginRegistrars = listOf(KermitCompilerPluginRegistrar(stripBelow))
    inheritClassPath = true
}.compile()
