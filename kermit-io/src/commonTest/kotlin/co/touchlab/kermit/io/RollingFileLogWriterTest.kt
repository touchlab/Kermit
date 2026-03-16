/*
 * Copyright (c) 2026 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.io

import co.touchlab.kermit.Severity
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readString

class RollingFileLogWriterTest {

    private fun createTempDir(): Path {
        val base = Path(SystemFileSystem.resolve(Path(".")), "build", "tmp", "test-logs-${randomSuffix()}")
        SystemFileSystem.createDirectories(base)
        return base
    }

    private fun randomSuffix(): String = (0..7).map { ('a'..'z').random() }.joinToString("")

    private fun readFileText(path: Path): String = SystemFileSystem.source(path).buffered().readString()

    private fun createWriter(
        dir: Path,
        fileName: String = "test",
        rollOnSize: Long = 10 * 1024 * 1024,
        maxLogFiles: Int = 5,
    ): RollingFileLogWriter = RollingFileLogWriter(
        config = RollingFileLogWriterConfig(
            logFileName = fileName,
            logFilePath = dir,
            rollOnSize = rollOnSize,
            maxLogFiles = maxLogFiles,
            prependTimestamp = false,
            logTag = false,
        ),
    )

    private fun deleteRecursively(dir: Path) {
        try {
            SystemFileSystem.list(dir).forEach { path ->
                SystemFileSystem.delete(path)
            }
            SystemFileSystem.delete(dir)
        } catch (_: Exception) {
            // best-effort cleanup
        }
    }

    /**
     * Give the writer coroutine time to process log messages.
     */
    private suspend fun waitForWrite() {
        delay(200)
    }

    @Test
    fun writesLogMessageToFile() = runBlocking {
        val dir = createTempDir()
        try {
            val writer = createWriter(dir)
            writer.log(Severity.Info, "Hello Kermit", "TestTag", null)
            waitForWrite()

            val logFile = Path(dir, "test.log")
            assertTrue(SystemFileSystem.exists(logFile), "Log file should exist")

            val content = readFileText(logFile)
            assertTrue(content.contains("Hello Kermit"), "Log file should contain the message")
        } finally {
            deleteRecursively(dir)
        }
    }

    @Test
    fun appendsMultipleMessages() = runBlocking {
        val dir = createTempDir()
        try {
            val writer = createWriter(dir)
            writer.log(Severity.Info, "First message", "Tag", null)
            writer.log(Severity.Warn, "Second message", "Tag", null)
            waitForWrite()

            val content = readFileText(Path(dir, "test.log"))
            assertTrue(content.contains("First message"))
            assertTrue(content.contains("Second message"))
        } finally {
            deleteRecursively(dir)
        }
    }

    @Test
    fun rollsFileWhenSizeExceeded() = runBlocking {
        val dir = createTempDir()
        try {
            // Use a very small roll size to trigger rolling
            val writer = createWriter(dir, rollOnSize = 50)

            // Write enough to exceed the roll size
            repeat(5) {
                writer.log(Severity.Info, "Log message number $it that is long enough to exceed the limit", "Tag", null)
                waitForWrite()
            }

            val primaryLog = Path(dir, "test.log")
            val rolledLog = Path(dir, "test-1.log")
            val rolledLog2 = Path(dir, "test-2.log")
            val rolledLog3 = Path(dir, "test-3.log")
            val rolledLog4 = Path(dir, "test-4.log")

            assertTrue(SystemFileSystem.exists(primaryLog), "Primary log file should exist")
            assertTrue(SystemFileSystem.exists(rolledLog), "Rolled log file should exist")
            assertTrue(SystemFileSystem.exists(rolledLog2), "Rolled log file should exist")
            assertTrue(SystemFileSystem.exists(rolledLog3), "Rolled log file should exist")
            assertTrue(SystemFileSystem.exists(rolledLog4), "Rolled log file should exist")
        } finally {
            deleteRecursively(dir)
        }
    }

    @Test
    fun deletesOldestFileWhenMaxReached() = runBlocking {
        val dir = createTempDir()
        try {
            // maxLogFiles=2 means we keep test.log and test-1.log, delete anything older
            val writer = createWriter(dir, rollOnSize = 50, maxLogFiles = 2)

            // Write enough messages to trigger multiple rolls
            repeat(10) {
                writer.log(Severity.Info, "Log message number $it that is long enough to exceed the small limit", "Tag", null)
                waitForWrite()
            }

            val primaryLog = Path(dir, "test.log")
            val rolledLog1 = Path(dir, "test-1.log")
            val rolledLog2 = Path(dir, "test-2.log")

            assertTrue(SystemFileSystem.exists(primaryLog), "Primary log should exist")
            assertTrue(SystemFileSystem.exists(rolledLog1), "First rolled log should exist")
            assertFalse(SystemFileSystem.exists(rolledLog2), "Second rolled log should not exist (maxLogFiles=2)")
        } finally {
            deleteRecursively(dir)
        }
    }

    @Test
    fun includesThrowableStackTrace() = runBlocking {
        val dir = createTempDir()
        try {
            val writer = createWriter(dir)
            val exception = RuntimeException("test error")
            writer.log(Severity.Error, "Error occurred", "Tag", exception)
            waitForWrite()

            val content = readFileText(Path(dir, "test.log"))
            assertTrue(content.contains("Error occurred"))
            assertTrue(content.contains("RuntimeException"), "Should contain exception class name")
            assertTrue(content.contains("test error"), "Should contain exception message")
        } finally {
            deleteRecursively(dir)
        }
    }
}
