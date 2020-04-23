package co.touchlab.kermit


class ConsoleLogger : Logger() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        console.log(severity.name, tag, message)
    }

    override fun v(message: String, tag: String, throwable: Throwable? = null){
        console.log(tag,message,throwable)
    }
    override fun d(message: String, tag: String, throwable: Throwable? = null){
        console.log(tag,message,throwable)
    }
    override fun i(message: String, tag: String, throwable: Throwable? = null){
        console.info(tag,message,throwable)
    }
    override fun w(message: String, tag: String, throwable: Throwable? = null){
        console.warn(tag,message,throwable)
    }
    override fun e(message: String, tag: String, throwable: Throwable? = null){
        console.error(tag,message,throwable)
    }
    override fun wtf(message: String, tag: String, throwable: Throwable? = null){
        console.error(tag,message,throwable)
    }
}