/*
 * Copyright (c) 2025 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.ktor

import co.touchlab.kermit.Logger as KermitLogger
import co.touchlab.kermit.LoggerConfig
import co.touchlab.kermit.Severity
import io.ktor.client.plugins.logging.Logger as KtorLogger

class KermitKtorLogger(private val severity: Severity, private val logger: KermitLogger) : KtorLogger {

    constructor(
        severity: Severity,
        config: LoggerConfig,
        tag: String = "",
    ) : this(
        logger = KermitLogger(config = config, tag = tag),
        severity = severity,
    )

    override fun log(message: String) {
        when (severity) {
            Severity.Verbose -> logger.v(messageString = message)
            Severity.Debug -> logger.d(messageString = message)
            Severity.Info -> logger.i(messageString = message)
            Severity.Warn -> logger.w(messageString = message)
            Severity.Error -> logger.e(messageString = message)
            Severity.Assert -> logger.a(messageString = message)
        }
    }
}
