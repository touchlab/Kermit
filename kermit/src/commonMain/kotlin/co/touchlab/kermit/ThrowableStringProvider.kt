package co.touchlab.kermit

interface ThrowableStringProvider {
    fun getThrowableString(throwable: Throwable) = buildString {
        fun Throwable.printCause(depth: Int = 0) {
            append("Caused by: ${toString()}\n")
            cause?.let {
                if (it !== this && depth < 2)
                    it.printCause(depth + 1)
            }
        }

        append("$throwable\n")
        throwable.cause?.printCause()
    }
}

expect class PlatformThrowableStringProvider() : ThrowableStringProvider