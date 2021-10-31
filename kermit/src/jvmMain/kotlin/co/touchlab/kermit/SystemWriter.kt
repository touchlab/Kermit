package co.touchlab.kermit

class SystemWriter : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val str = "$severity: ($tag) $message"
        if (severity == Severity.Error) {
            System.err.println(str)
        } else {
            println(str)
        }
        throwable?.let {
            val thString = it.stackTraceToString()
            if (severity == Severity.Error) {
                System.err.println(thString)
            } else {
                println(thString)
            }
        }
    }
}
