package co.touchlab.kermit

class Kermit(
    private val loggerList: List<Logger> = listOf(
        CommonLogger()
    )
) {
    constructor(vararg loggers: Logger) : this(loggers.asList())
    constructor(logger: Logger) : this(listOf(logger))

    fun log(
        severity: Severity,
        tag: String?,
        throwable: Throwable?,
        message: () -> String
    ) {
        loggerList.forEach {
            if (it.isLoggable(severity)) it.log(
                severity,
                message(),
                tag,
                throwable
            )
        }
    }

    fun v(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Verbose)) {
                it.v(message(), tag, throwable)
            }
        }
    }

    fun d(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Debug)) {
                it.d(message(), tag, throwable)
            }
        }
    }

    fun i(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Info)) {
                it.i(message(), tag, throwable)
            }
        }
    }

    fun w(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Warn)) {
                it.w(message(), tag, throwable)
            }
        }
    }

    fun e(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Error)) {
                it.e(message(), tag, throwable)
            }
        }
    }

    fun wtf(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.forEach {
            if (it.isLoggable(Severity.Assert)) {
                it.wtf(message(), tag, throwable)
            }
        }
    }
}

