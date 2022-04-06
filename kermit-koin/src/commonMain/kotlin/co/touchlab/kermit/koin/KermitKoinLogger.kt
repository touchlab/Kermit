/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit.koin

import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE
import co.touchlab.kermit.Logger as KermitLogger
import org.koin.core.logger.Logger as KoinLogger

class KermitKoinLogger(private val logger: KermitLogger) : KoinLogger() {
    override fun log(level: Level, msg: MESSAGE) {
        when (level) {
            Level.DEBUG -> logger.d(msg)
            Level.INFO -> logger.i(msg)
            Level.ERROR -> logger.e(msg)
            Level.NONE -> {
                // do nothing
            }
        }
    }
}