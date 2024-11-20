/*
 * Copyright (c) 2024 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.io

import co.touchlab.kermit.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.io.*
import kotlinx.io.files.FileSystem
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * Implements a log writer that writes log messages to a rolling file.
 *
 * It also deletes old log files when the maximum number of log files is reached. We simply keep
 * approximately [RollingFileLogWriterConfig.rollOnSize] bytes in each log file,
 * and delete the oldest file when we have more than [RollingFileLogWriterConfig.maxLogFiles].
 *
 * Formatting is governed by the passed [MessageStringFormatter], but we do prepend a timestamp by default.
 * Turn this off via [RollingFileLogWriterConfig.prependTimestamp]
 *
 * Writes to the file are done by a different coroutine. The main reason for this is to make writes to the
 * log file sink thread-safe, and so that file rolling can be performed without additional synchronization
 * or locking. The channel that buffers log messages is currently unbuffered, so logging threads will block
 * until the I/O is complete. However, buffering could easily be introduced to potentially increase logging
 * throughput. The envisioned usage scenarios for this class probably do not warrant this.
 *
 * The recommended way to obtain the logPath on Android is:
 *
 * ```kotlin
 * Path(context.filesDir.path)
 * ```
 *
 * and on iOS this wil return the application's sandboxed document directory:
 *
 * ```kotlin
 * (NSFileManager.defaultManager.URLsForDirectory(NSDocumentDirectory, NSUserDomainMask).last() as NSURL).path!!
 * ```
 *
 * However, you can use any path that is writable by the application. This would generally be implemented by
 * platform-specific code.
 */
open class RollingFileLogWriter(
  private val config: RollingFileLogWriterConfig,
  private val messageStringFormatter: MessageStringFormatter = DefaultFormatter,
  private val clock: Clock = Clock.System,
  private val fileSystem: FileSystem = SystemFileSystem,
) : LogWriter() {
  @OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
  private val coroutineScope = CoroutineScope(
    newSingleThreadContext("RollingFileLogWriter") +
      SupervisorJob() +
      CoroutineName("RollingFileLogWriter") +
      CoroutineExceptionHandler { _, throwable ->
        // can't log it, we're the logger -- print to standard error
        println("RollingFileLogWriter: Uncaught exception in writer coroutine")
        throwable.printStackTrace()
      }
  )

  private val loggingChannel: Channel<Buffer> = Channel()

  init {
    coroutineScope.launch {
      writer()
    }
  }

  override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
    bufferLog(
      formatMessage(
        severity = severity,
        tag = Tag(tag),
        message = Message(message)
      ), throwable
    )
  }

  private fun bufferLog(message: String, throwable: Throwable?) {
    val log = buildString {
      append(clock.now().format(DateTimeComponents.Formats.ISO_DATE_TIME_OFFSET))
      append(" ")
      appendLine(message)
      if (throwable != null) {
        appendLine(throwable.stackTraceToString())
      }
    }
    loggingChannel.trySendBlocking(Buffer().apply { writeString(log) })
  }

  private fun formatMessage(severity: Severity, tag: Tag?, message: Message): String =
    messageStringFormatter.formatMessage(severity, if (config.logTag) tag else null, message)

  private fun maybeRollLogs(size: Long): Boolean {
    return if (size > config.rollOnSize) {
      rollLogs()
      true
    } else false
  }

  private fun rollLogs() {
    if (fileSystem.exists(pathForLogIndex(config.maxLogFiles - 1))) {
      fileSystem.delete(pathForLogIndex(config.maxLogFiles - 1))
    }
    (0..<(config.maxLogFiles  - 1)).reversed().forEach {
      val sourcePath = pathForLogIndex(it)
      val targetPath = pathForLogIndex(it + 1)
      if (fileSystem.exists(sourcePath)) {
        try {
          fileSystem.atomicMove(sourcePath, targetPath)
        } catch (e: IOException) {
          // we can't log it, we're the logger -- print to standard error
          println("RollingFileLogWriter: Failed to roll log file $sourcePath to $targetPath (sourcePath exists=${fileSystem.exists(sourcePath)})")
          e.printStackTrace()
        }
      }
    }
  }

  private fun pathForLogIndex(index: Int): Path =
    Path(config.logFilePath, if (index == 0) "${config.logFileName}.log" else "${config.logFileName}-$index.log")

  private suspend fun writer() {
    val logFilePath = pathForLogIndex(0)

    if (fileSystem.exists(logFilePath)) {
      maybeRollLogs(fileSizeOrZero(logFilePath))
    }

    fun createNewLogSink(): Sink = fileSystem
      .sink(logFilePath, append = true)
      .buffered()

    var currentLogSink: Sink = createNewLogSink()

    while (currentCoroutineContext().isActive) {
      // wait for data to be available, flush periodically
      val result = loggingChannel.receiveCatching()

      // check if logs need rolling
      val rolled = maybeRollLogs(fileSizeOrZero(logFilePath))
      if (rolled) {
        currentLogSink.close()
        currentLogSink = createNewLogSink()
      }

      result.getOrNull()?.transferTo(currentLogSink)

      // we could improve performance by flushing less frequently at the cost of potential data loss,
      // but this is a safe default
      currentLogSink.flush()
    }
  }

  private fun fileSizeOrZero(path: Path) = fileSystem.metadataOrNull(path)?.size ?: 0
}
