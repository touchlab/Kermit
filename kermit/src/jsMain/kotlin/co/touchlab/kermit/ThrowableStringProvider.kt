package co.touchlab.kermit

actual class PlatformThrowableStringProvider : ThrowableStringProvider {
    override fun getThrowableString(throwable: Throwable): String {
        return super.getThrowableString(throwable)
    }
}