package co.touchlab.kermit

import android.util.Log

class LogcatLogger : Logger() {

    private fun getSeverity(severity: Severity) = when (severity) {
        Severity.Verbose -> 2
        Severity.Debug -> 3
        Severity.Info -> 4
        Severity.Warn -> 5
        Severity.Error -> 6
        Severity.Assert -> 7
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        Log.println(getSeverity(severity), tag, message)
    }

    override fun v(message: String, tag: String, throwable: Throwable?) {
        Log.v(tag, message, throwable)
    }

    override fun d(message: String, tag: String, throwable: Throwable?) {
        Log.d(tag, message, throwable)
    }

    override fun i(message: String, tag: String, throwable: Throwable?) {
        Log.i(tag, message, throwable)
    }

    override fun w(message: String, tag: String, throwable: Throwable?) {
        Log.w(tag, message, throwable)
    }

    override fun e(message: String, tag: String, throwable: Throwable?) {
        Log.e(tag, message, throwable)
    }

    override fun wtf(message: String, tag: String, throwable: Throwable?) {
        Log.wtf(tag, message, throwable)
    }
}