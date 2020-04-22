package co.touchlab.kermit

import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestLogger(val loggable:Severity = Severity.Verbose) : Logger() {
    data class LogEntry(
        val severity: Severity,
        val message: String,
        val tag: String?,
        val throwable: Throwable?
    )

    val _logs = mutableListOf<LogEntry>()
    val logs: List<LogEntry>
        get() = _logs.toList()

    fun assertCount(count: Int) {
        assertEquals(count, _logs.size)
    }

    fun assertLast(check: LogEntry.() -> Boolean) {
        assertTrue(_logs.last().check())
    }

    fun reset() {
        _logs.clear()
    }

    override fun isLoggable(severity: Severity): Boolean {
        return severity.ordinal >= loggable.ordinal
    }

    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        _logs.add(LogEntry(severity, message, tag, throwable))
    }
}