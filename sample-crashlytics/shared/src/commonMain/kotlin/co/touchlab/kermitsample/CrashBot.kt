/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample

import kotlin.random.Random

class CrashBot() {

    fun goCrash() {
        internalDispatch()
    }

    private val dispatchCall = {
        val door = Random.nextInt(4)
        when(door){
            0 -> goCrash0()
            1 -> goCrash1()
            2 -> goCrash2()
            3 -> goCrash3()
        }
    }
    private fun internalDispatch() = dispatchCall()
    private fun internalDispatch1() = dispatchCall()
    private fun internalDispatch2() = dispatchCall()

    private fun goCrash0() {
        internalDispatch()
    }

    private fun goCrash1() {
        internalDispatch1()
    }

    private fun goCrash2() {
        internalDispatch2()
    }

    private fun goCrash3() {
        okCrash()
    }

    private fun okCrash(){
        throw IllegalStateException("Nap time ...")
    }
}