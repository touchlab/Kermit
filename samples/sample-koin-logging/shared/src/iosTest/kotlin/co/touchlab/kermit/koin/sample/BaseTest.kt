package co.touchlab.kermit.koin.sample

import kotlinx.coroutines.*
import platform.CoreFoundation.CFRunLoopGetCurrent
import platform.CoreFoundation.CFRunLoopRun
import platform.CoreFoundation.CFRunLoopStop

actual abstract class BaseTest {
  @OptIn(DelicateCoroutinesApi::class)
  actual fun <T> runTest(block: suspend CoroutineScope.() -> T) {
    var error: Throwable? = null
    GlobalScope.launch(Dispatchers.Main) {
      try {
        block()
      } catch (t: Throwable) {
        error = t
      } finally {
        CFRunLoopStop(CFRunLoopGetCurrent())
      }
    }
    CFRunLoopRun()
    error?.also { throw it }
  }
}