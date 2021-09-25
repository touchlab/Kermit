/*
 * Copyright (c) 2021 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsamplecrashlog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.touchlab.kermitsample.CrashBot
import co.touchlab.kermitsample.SampleCommon
import co.touchlab.kermitsamplecrashlog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sampleCommon = SampleCommon()
        binding.clickCount.setOnClickListener{
           sampleCommon.onClick()
        }
        binding.logException.setOnClickListener{
            sampleCommon.logException()
        }
        binding.crash.setOnClickListener {
            CrashBot().goCrash()
        }
    }
}