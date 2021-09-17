/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package co.touchlab.kermit

interface LoggerConfig {
    val minSeverity:Severity
    val logWriterList: List<LogWriter>
    companion object {
        @Suppress("unused")
        val default = StaticConfig()
    }
}

data class StaticConfig(
    override val minSeverity: Severity = DEFAULT_MIN_SEVERITY,
    override val logWriterList: List<LogWriter> = listOf(CommonWriter()),
): LoggerConfig