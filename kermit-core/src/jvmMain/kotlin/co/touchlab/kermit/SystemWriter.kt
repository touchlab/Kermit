package co.touchlab.kermit

class SystemWriter(private val messageStringFormatter: MessageStringFormatter = DefaultFormatter) : LogWriter() {

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        val str = messageStringFormatter.formatMessage(severity, Tag(tag), Message(message), throwable)
        if (severity == Severity.Error) {
            System.err.println(str)
        } else {
            println(str)
        }
    }
}
