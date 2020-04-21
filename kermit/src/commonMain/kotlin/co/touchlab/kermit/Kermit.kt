package co.touchlab.kermit

class Kermit(
    private val loggerList: List<Logger> = listOf(
        CommonLogger()
    ),private val defaultTag:String = "Kermit"
) {


    constructor(vararg loggers: Logger) : this(loggers.asList())
    constructor(logger: Logger) : this(listOf(logger))

    fun withTag(defaultTag:String) : Kermit {
        return Kermit(this.loggerList,defaultTag)
    }

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
                tag ?: defaultTag,
                throwable
            )
        }
    }

    fun v(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Verbose)}
            .forEach { it.v(message(), tag, throwable)}
    }

    fun v(withMessage: () -> String) {
        v(message = withMessage)
    }

    fun v(withThrowable: Throwable?, message: () -> String) {
        v(throwable = withThrowable, message = message)
    }

    fun d(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Debug)}
            .forEach { it.d(message(), tag, throwable)}
    }

    fun d(withMessage: () -> String) {
        d(message = withMessage)
    }

    fun d(withThrowable: Throwable?, message: () -> String) {
        d(throwable = withThrowable, message = message)
    }

    fun i(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Info)}
            .forEach { it.i(message(), tag, throwable)}
    }

    fun i(withMessage: () -> String) {
        i(message = withMessage)
    }

    fun i(withThrowable: Throwable?, message: () -> String) {
        i(throwable = withThrowable, message = message)
    }

    fun w(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Warn)}
            .forEach { it.w(message(), tag, throwable)}
    }

    fun w(withMessage: () -> String) {
        w(message = withMessage)
    }

    fun w(withThrowable: Throwable?, message: () -> String) {
        w(throwable = withThrowable, message = message)
    }

    fun e(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Error)}
            .forEach { it.e(message(), tag, throwable)}
    }

    fun e(withMessage: () -> String) {
        e(message = withMessage)
    }

    fun e(withThrowable: Throwable?, message: () -> String) {
        e(throwable = withThrowable, message = message)
    }

    fun wtf(tag: String = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Assert)}
            .forEach { it.wtf(message(), tag, throwable)}
    }

    fun wtf(withMessage: () -> String) {
        wtf(message = withMessage)
    }

    fun wtf(withThrowable: Throwable?, message: () -> String) {
        wtf(throwable = withThrowable, message = message)
    }
}

