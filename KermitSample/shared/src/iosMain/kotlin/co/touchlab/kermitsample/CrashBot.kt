/*
 * Copyright (c) 2020. Touchlab, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample


import co.touchlab.crashkios.catchAndReport
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.random.Random

class CrashBot() {
    private val worker = Worker.start()

    fun goCrash() {
        internalDispatch()
    }

    fun goCrashBackground() {
        worker.execute(TransferMode.SAFE, {this.freeze()}) {
            try {
                it.goCrash()
            } catch (t: Throwable) {
                terminate(t)
            }
        }
    }

    fun differentPath(){
        throw SampleException("Nap time 2 ...")
    }

    fun manualCatch() {
        manualCatchPrivate()
    }

    private fun internalDispatch() {
        val door = Random.nextInt(5)
        when(door){
            0 -> goCrash0()
            1 -> goCrash1()
            2 -> goCrash2()
            3 -> goCrash3()
            4 -> goCrash4()
        }
    }

    private fun internalDispatch1() {
        val door = Random.nextInt(5)
        when(door){
            0 -> goCrash0()
            1 -> goCrash1()
            2 -> goCrash2()
            3 -> goCrash3()
            4 -> goCrash4()
        }
    }

    private fun internalDispatch2() {
        val door = Random.nextInt(5)
        when(door){
            0 -> goCrash0()
            1 -> goCrash1()
            2 -> goCrash2()
            3 -> goCrash3()
            4 -> goCrash4()
        }
    }

    private fun internalDispatch3() {
        val door = Random.nextInt(5)
        when(door){
            0 -> goCrash0()
            1 -> goCrash1()
            2 -> goCrash2()
            3 -> goCrash3()
            4 -> goCrash4()
        }
    }



    private fun manualCatchPrivate() = catchAndReport {
        goCrash()
    }

    private fun goCrash0() {
        println("goCrash0")
        internalDispatch()
    }

    private fun goCrash1() {
        println("goCrash1")
        internalDispatch1()
    }

    private fun goCrash2() {
        println("goCrash2")
        internalDispatch2()
    }

    private fun goCrash3() {
        println("goCrash3")
        internalDispatch3()
    }

    private fun goCrash4() {
        println("goCrash4")
        wtfCrash2()
    }

    private fun wtfCrash2() {
        moreLayers()
    }

    private fun moreLayers(){
        throw SampleException("Nap time ...")
    }
}

class SampleException(message:String? = null, cause:Throwable? = null): Exception(message, cause)

@SymbolName("TerminateWithUnhandledException")
private external fun terminate(t: Throwable)