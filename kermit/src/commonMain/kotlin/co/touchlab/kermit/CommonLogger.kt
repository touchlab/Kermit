package co.touchlab.kermit

class CommonLogger(private val throwableStringProvider: ThrowableStringProvider = PlatformThrowableStringProvider()) :
    Logger() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        println("$severity: ($tag) $message")
        throwable?.let {
            println(throwableStringProvider.getThrowableString(it))
        }
    }
}
