/*
 * Copyright (c) 2026 Touchlab
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

package co.touchlab.kermit.coil

import co.touchlab.kermit.Logger as KermitLogger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.mutableLoggerConfigInit
import coil3.util.Logger as CoilLogger
import kotlin.jvm.JvmOverloads

class KermitCoilLogger @JvmOverloads constructor(config: LoggerConfig, tag: String = "Coil", private val separator: String = "/") :
    CoilLogger {

    @JvmOverloads
    constructor(logger: KermitLogger, separator: String = "/") : this(
        config = logger.config,
        tag = logger.tag,
        separator = separator,
    )

    private val logger: KermitLogger = KermitLogger(
        config = mutableLoggerConfigInit(
            logWriters = config.logWriterList.toTypedArray(),
            minSeverity = config.minSeverity,
        ),
        tag = tag,
    )

    override var minLevel: CoilLogger.Level
        get() = logger.config.minSeverity.toCoilLoggerLevel()
        set(value) {
            logger.mutableConfig.minSeverity = value.toKermitSeverity()
        }

    override fun log(tag: String, level: CoilLogger.Level, message: String?, throwable: Throwable?) {
        if (message == null && throwable == null) return
        val separator = if (logger.tag.isNotEmpty() && tag.isNotEmpty()) separator else ""
        val tag = "${logger.tag}$separator$tag"
        logger.log(level.toKermitSeverity(), tag, throwable, message.orEmpty())
    }
}
