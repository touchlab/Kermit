/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

import platform.Foundation.*

open class XcodeSeverityWriter : CommonWriter() {
    private val df:NSDateFormatter = NSDateFormatter().apply {
        dateFormat = "HH:mm:ss.SSS"
    }

    override fun formatMessage(severity: Severity, message: String, tag: String, throwable: Throwable?): String =
        "${df.stringFromDate(NSDate.now)} ${emojiPrefix(severity)} $severity: ($tag) $message"

    //If this looks familiar, yes, it came directly from Napier :) https://github.com/AAkira/Napier#darwinios-macos-watchos-tvosintelapple-silicon
    open fun emojiPrefix(severity: Severity): String = when (severity) {
        Severity.Verbose -> "⚪️"
        Severity.Debug -> "🔵"
        Severity.Info -> "🟢"
        Severity.Warn -> "🟡"
        Severity.Error -> "🔴"
        Severity.Assert -> "🟤️"
    }
}