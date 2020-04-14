package co.touchlab.kermit

class Kermit(
    private val loggerList: List<Logger> = listOf(
        CommonLogger()
    )
) {
    private var defaultTag:String? = null

    constructor(vararg loggers: Logger) : this(loggers.asList())
    constructor(logger: Logger) : this(listOf(logger))

    fun withTag(defaultTag:String) : Kermit {
        val taggedKermit = Kermit(loggerList)
        taggedKermit.defaultTag = defaultTag
        return taggedKermit
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

    fun v(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Verbose)}
            .forEach { it.v(message(), tag, throwable)}
    }

    fun d(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Debug)}
            .forEach { it.d(message(), tag, throwable)}
    }

    fun i(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Info)}
            .forEach { it.i(message(), tag, throwable)}
    }

    fun w(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Warn)}
            .forEach { it.w(message(), tag, throwable)}
    }

    fun e(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Error)}
            .forEach { it.e(message(), tag, throwable)}
    }

    fun wtf(tag: String? = defaultTag, throwable: Throwable? = null, message: () -> String) {
        loggerList.filter {it.isLoggable(Severity.Assert)}
            .forEach { it.wtf(message(), tag, throwable)}
    }
}

