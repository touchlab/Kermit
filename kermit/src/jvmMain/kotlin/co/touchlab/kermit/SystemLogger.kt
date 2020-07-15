package co.touchlab.kermit

class SystemLogger(private val throwableStringProvider: ThrowableStringProvider = PlatformThrowableStringProvider()) : Logger() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val str = "$severity: ($tag) $message\")"
        if (severity == Severity.Error) {
            System.err.println(str)
        } else {
            println(str)
        }
        throwable?.let {
            println(throwableStringProvider.getThrowableString(it))
        }
    }
}
