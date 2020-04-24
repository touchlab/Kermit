package co.touchlab.kermitsample.js

import co.touchlab.kermit.CommonLogger
import co.touchlab.kermit.Kermit
import co.touchlab.kermitsample.SampleCommon
import co.touchlab.kermitsample.getConsoleLogger
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.js.onClickFunction
import kotlin.browser.document

val kermit = Kermit(listOf(CommonLogger(), getConsoleLogger()), "jsTag")
val common = SampleCommon(kermit)

fun main() {
    kermit.i { "loaded" }

    document.body?.append {
        div {
            button {
                text("Click Here")
                onClickFunction = {
                    common.onClick()
                }
            }
        }
    }
}
