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
        loggerList.filter { it.isLoggable(severity) }. forEach {
            it.log(
                severity,
                message(),
                tag,
                throwable
            )
        }
    }

    fun v(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Verbose)}
            .forEach { it.v(message(), tag, throwable)}
    }

    fun d(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Debug)}
            .forEach { it.d(message(), tag, throwable)}
    }

    fun i(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Info)}
            .forEach { it.i(message(), tag, throwable)}
    }

    fun w(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Warn)}
            .forEach { it.w(message(), tag, throwable)}
    }

    fun e(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Error)}
            .forEach { it.e(message(), tag, throwable)}
    }

    fun wtf(tag: String? = null, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Assert)}
            .forEach { it.wtf(message(), tag, throwable)}
    }
}

