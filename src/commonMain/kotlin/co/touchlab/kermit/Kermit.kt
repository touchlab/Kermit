package co.touchlab.kermit

class Kermit(private val loggers: List<Logger> = listOf(
    CommonLogger()
)) {
    constructor(vararg logger: Logger) : this(logger.asList())
    constructor(logger:Logger): this(listOf(logger))

    fun v(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.v( message, tag, throwable) }
    }

    fun d(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.d(message, tag, throwable) }
    }

    fun i(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.i(message, tag, throwable) }
    }

    fun w(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.i(message, tag, throwable) }
    }

    fun e(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.e(message, tag, throwable) }
    }

    fun wtf(message: String, tag: String? = null, throwable: Throwable? = null) {
        loggers.forEach { it.wtf(message, tag, throwable) }
    }
}

