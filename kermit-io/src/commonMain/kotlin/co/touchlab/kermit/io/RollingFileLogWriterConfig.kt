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

import kotlinx.io.files.Path

/**
 * @param logFileName the name of the log file, Kermit will add the filetype ".log". If there is rollover the file will have an index appended to it, e.g. "app.log.1"
 * @param logFilePath the path to the directory where log files will be stored
 * @param rollOnSize the size in bytes at which the log file will roll over to a new file
 * @param maxLogFiles the maximum number of log files to keep. If the LogWriter reaches the max then it will delete the oldest log file when rolling over.
 * @param logTag whether to include the log tag in each log message
 * @param prependTimestamp whether to prepend a timestamp to each log message
 */
data class RollingFileLogWriterConfig(
    val logFileName: String,
    val logFilePath: Path,
    val rollOnSize: Long = 10 * 1024 * 1024, // 10MB
    val maxLogFiles: Int = 5,
    val logTag: Boolean = true,
    val prependTimestamp: Boolean = true,
)
