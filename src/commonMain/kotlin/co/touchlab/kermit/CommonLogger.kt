package co.touchlab.kermit

class CommonLogger : Logger {
    override fun log(severity: Severity, message: String, tag: String?, throwable: Throwable?) {
        println("$severity: ($tag) $message")
    }
}