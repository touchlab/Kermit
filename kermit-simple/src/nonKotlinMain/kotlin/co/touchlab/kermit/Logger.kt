/*
 * Copyright (c) 2022 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermit

fun Logger.v(message: () -> String) {
    if (config.minSeverity <= Severity.Verbose) {
        log(Severity.Verbose, tag, null, message())
    }
}

fun Logger.v(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Verbose) {
        log(Severity.Verbose, tag, throwable, message())
    }
}

fun Logger.v(messageString: String) {
    if (config.minSeverity <= Severity.Verbose) {
        log(Severity.Verbose, tag, null, messageString)
    }
}

fun Logger.v(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Verbose) {
        log(Severity.Verbose, tag, throwable, messageString)
    }
}

fun Logger.d(message: () -> String) {
    if (config.minSeverity <= Severity.Debug) {
        log(Severity.Debug, tag, null, message())
    }
}

fun Logger.d(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Debug) {
        log(Severity.Debug, tag, throwable, message())
    }
}

fun Logger.d(messageString: String) {
    if (config.minSeverity <= Severity.Debug) {
        log(Severity.Debug, tag, null, messageString)
    }
}

fun Logger.d(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Debug) {
        log(Severity.Debug, tag, throwable, messageString)
    }
}

fun Logger.i(message: () -> String) {
    if (config.minSeverity <= Severity.Info) {
        log(Severity.Info, tag, null, message())
    }
}

fun Logger.i(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Info) {
        log(Severity.Info, tag, throwable, message())
    }
}

fun Logger.i(messageString: String) {
    if (config.minSeverity <= Severity.Info) {
        log(Severity.Info, tag, null, messageString)
    }
}

fun Logger.i(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Info) {
        log(Severity.Info, tag, throwable, messageString)
    }
}

fun Logger.w(message: () -> String) {
    if (config.minSeverity <= Severity.Warn) {
        log(Severity.Warn, tag, null, message())
    }
}

fun Logger.w(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Warn) {
        log(Severity.Warn, tag, throwable, message())
    }
}

fun Logger.w(messageString: String) {
    if (config.minSeverity <= Severity.Warn) {
        log(Severity.Warn, tag, null, messageString)
    }
}

fun Logger.w(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Warn) {
        log(Severity.Warn, tag, throwable, messageString)
    }
}

fun Logger.e(message: () -> String) {
    if (config.minSeverity <= Severity.Error) {
        log(Severity.Error, tag, null, message())
    }
}

fun Logger.e(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Error) {
        log(Severity.Error, tag, throwable, message())
    }
}

fun Logger.e(messageString: String) {
    if (config.minSeverity <= Severity.Error) {
        log(Severity.Error, tag, null, messageString)
    }
}

fun Logger.e(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Error) {
        log(Severity.Error, tag, throwable, messageString)
    }
}

fun Logger.a(message: () -> String) {
    if (config.minSeverity <= Severity.Assert) {
        log(Severity.Assert, tag, null, message())
    }
}

fun Logger.a(throwable: Throwable, message: () -> String) {
    if (config.minSeverity <= Severity.Assert) {
        log(Severity.Assert, tag, throwable, message())
    }
}

fun Logger.a(messageString: String) {
    if (config.minSeverity <= Severity.Assert) {
        log(Severity.Assert, tag, null, messageString)
    }
}

fun Logger.a(messageString: String, throwable: Throwable) {
    if (config.minSeverity <= Severity.Assert) {
        log(Severity.Assert, tag, throwable, messageString)
    }
}

// These are all simple accessor methods to make global calls from (mostly darwin) native simpler
fun withTag(tag: String): Logger = Logger.withTag(tag)

fun v(message: () -> String) {
    Logger.v(message)
}

fun v(string: String) {
    Logger.v(string)
}

fun v(throwable: Throwable, message: () -> String) {
    Logger.v(throwable, message)
}

fun v(message: String, throwable: Throwable) {
    Logger.v(message, throwable)
}

fun d(message: () -> String) {
    Logger.d(message)
}

fun d(string: String) {
    Logger.d(string)
}

fun d(throwable: Throwable, message: () -> String) {
    Logger.d(throwable, message)
}

fun d(message: String, throwable: Throwable) {
    Logger.d(message, throwable)
}

fun i(message: () -> String) {
    Logger.i(message)
}

fun i(string: String) {
    Logger.i(string)
}

fun i(throwable: Throwable, message: () -> String) {
    Logger.i(throwable, message)
}

fun i(message: String, throwable: Throwable) {
    Logger.i(message, throwable)
}

fun w(message: () -> String) {
    Logger.w(message)
}

fun w(string: String) {
    Logger.w(string)
}

fun w(throwable: Throwable, message: () -> String) {
    Logger.w(throwable, message)
}

fun w(message: String, throwable: Throwable) {
    Logger.w(message, throwable)
}

fun e(message: () -> String) {
    Logger.e(message)
}

fun e(string: String) {
    Logger.e(string)
}

fun e(throwable: Throwable, message: () -> String) {
    Logger.e(throwable, message)
}

fun e(message: String, throwable: Throwable) {
    Logger.e(message, throwable)
}

fun a(message: () -> String) {
    Logger.a(message)
}

fun a(string: String) {
    Logger.a(string)
}

fun a(throwable: Throwable, message: () -> String) {
    Logger.a(throwable, message)
}

fun a(message: String, throwable: Throwable) {
    Logger.a(message, throwable)
}
