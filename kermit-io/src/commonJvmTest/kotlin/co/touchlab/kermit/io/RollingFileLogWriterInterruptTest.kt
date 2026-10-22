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
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * Reproduces https://github.com/touchlab/Kermit/issues/480
 *
 * `RollingFileLogWriter.bufferLog` used to use [kotlinx.coroutines.channels.trySendBlocking] directly. That only avoids blocking while the
 * channel has room; otherwise it falls back to `runBlocking { send(...) }`, and `runBlocking` throws [InterruptedException] immediately if
 * the calling thread's interrupt flag is set.
 *
 * So logging from a thread that the platform has interrupted (Android sync adapters, `ExecutorService.shutdownNow()`, ...) crashed the
 * caller. The reported stack trace was:
 * ```
 * java.lang.InterruptedException
 *     kotlinx.coroutines.BlockingCoroutine.joinBlocking
 *     kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking
 *     kotlinx.coroutines.channels.ChannelsKt__ChannelsKt.trySendBlocking
 *     co.touchlab.kermit.io.RollingFileLogWriter.bufferLog
 *     co.touchlab.kermit.io.RollingFileLogWriter.log
 * ```
 */
class RollingFileLogWriterInterruptTest {

    @Test
    fun logFromInterruptedThreadDoesNotThrow() {
        val dir = createTempDir()
        try {
            // rollOnSize = 0 plus a large maxLogFiles makes rollLogs() walk thousands of paths for every single message, so the writer
            // coroutine drains the channel far slower than this thread can fill it. That keeps the channel buffer full, which is what
            // deterministically forces the send onto its blocking path instead of relying on a race.
            val writer = createWriter(dir, rollOnSize = 0, maxLogFiles = 20_000)

            val thrown = AtomicReference<Throwable?>(null)
            val interruptFlagSurvived = AtomicBoolean(false)
            val logger = Thread {
                repeat(LOGGING_CHANNEL_CAPACITY + 1) { writer.log(Severity.Info, "fill the channel buffer $it", "Tag", null) }

                // Simulates the platform interrupting the logging thread (e.g. AbstractThreadedSyncAdapter cancelling a sync).
                Thread.currentThread().interrupt()
                try {
                    // An attempt can still find a free slot if the writer just drained one, so keep logging until the buffer is full again.
                    repeat(8) { writer.log(Severity.Info, "logged from an interrupted thread $it", "Tag", null) }
                } catch (t: Throwable) {
                    thrown.set(t)
                } finally {
                    // The logger must not swallow the caller's cancellation signal. Read the flag before clearing it for the test runner.
                    interruptFlagSurvived.set(Thread.interrupted())
                }
            }

            logger.start()
            logger.join(30_000)
            assertTrue(!logger.isAlive, "Logging thread did not finish")

            val error = thrown.get()
            if (error != null) {
                fail("Logging from an interrupted thread threw '${error.message}'", error)
            }
            assertTrue(interruptFlagSurvived.get(), "Logging cleared the thread's interrupt flag")
        } finally {
            deleteRecursively(dir)
        }
    }

    private fun createTempDir(): Path {
        val base = Path(SystemFileSystem.resolve(Path(".")), "build", "tmp", "test-logs-interrupt-${randomSuffix()}")
        SystemFileSystem.createDirectories(base)
        return base
    }

    private fun randomSuffix(): String = (0..7).map { ('a'..'z').random() }.joinToString("")

    private fun createWriter(dir: Path, rollOnSize: Long, maxLogFiles: Int): RollingFileLogWriter = RollingFileLogWriter(
        config = RollingFileLogWriterConfig(
            logFileName = "test",
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
}
