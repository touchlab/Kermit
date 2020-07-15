/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

actual class PlatformThrowableStringProvider : ThrowableStringProvider {

    override fun getThrowableString(throwable: Throwable): String {
        return dumpStackTrace(throwable)
    }

    // Adapted from native printStackTrace to let us include it in our log
    private fun dumpStackTrace(throwable: Throwable): String = buildString {
        dumpStackTrace(throwable) { appendln(it) }
    }

    private inline fun writeStackTraceElements(throwable: Throwable, writeln: (String) -> Unit) {
        for (element in throwable.getStackTrace()) {
            writeln("        at $element")
        }
    }

    private inline fun dumpStackTrace(throwable: Throwable, crossinline writeln: (String) -> Unit) {
        writeln(throwable.toString())

        writeStackTraceElements(throwable, writeln)

        var cause = throwable.cause
        while (cause != null) {
            writeln("Caused by: $cause")
            writeStackTraceElements(cause, writeln)
            cause = cause.cause
        }
    }
}
