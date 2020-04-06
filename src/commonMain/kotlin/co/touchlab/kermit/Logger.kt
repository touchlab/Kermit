package co.touchlab.kermit

interface Logger {
    fun isLoggable(priority: Int): Boolean = true

    fun log(severity: Severity, message: String, tag: String? = null, throwable: Throwable? = null)

    fun v(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Verbose, message, tag, throwable)

    fun d(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Debug, message, tag, throwable)

    fun i(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Info, message, tag, throwable)

    fun w(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Warn, message, tag, throwable)

    fun e(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Error, message, tag, throwable)

    fun wtf(message: String, tag: String? = null, throwable: Throwable? = null) =
        log(Severity.Assert, message, tag, throwable)
}