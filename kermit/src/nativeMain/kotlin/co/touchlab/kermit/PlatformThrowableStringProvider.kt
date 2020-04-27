package co.touchlab.kermit

actual class PlatformThrowableStringProvider : ThrowableStringProvider {

    override fun getThrowableString(throwable: Throwable): String {
        return dumpStackTrace(throwable)
    }

    //Adapted from native printStackTrace to let us include it in our log
    private fun dumpStackTrace(throwable: Throwable): String = buildString {
        dumpStackTrace(throwable) { appendln(it) }
    }

    private inline fun writeStackTraceElements(throwable: Throwable, writeln: (String) -> Unit) {
        for (element in throwable.getStackTrace()) {
            writeln("        at $element")
        }
    }

    private inline fun dumpStackTrace(throwable: Throwable, crossinline writeln: (String) -> Unit) {
        writeln(throwable.toString())

        writeStackTraceElements(throwable, writeln)

        var cause = throwable.cause
        while (cause != null) {
            writeln("Caused by: $cause")
            writeStackTraceElements(cause, writeln)
            cause = cause.cause
        }
    }
}