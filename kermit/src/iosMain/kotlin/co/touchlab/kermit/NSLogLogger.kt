package co.touchlab.kermit

import platform.Foundation.NSLog

class NSLogLogger : Logger() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        NSLog("%s: (%s) %s", severity.name, tag, message)
        throwable?.let {
            val string = PlatformThrowableStringProvider().getThrowableString(it)
            NSLog("%s", string)
        }
    }
}