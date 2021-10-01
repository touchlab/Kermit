/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.KermitSample

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import co.touchlab.KermitSample.databinding.FragmentFirstBinding
import co.touchlab.kermit.Logger
import co.touchlab.kermitsample.SampleCommon

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(R.layout.fragment_first) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Logger.withTag("FirstFragment").v("First fragment loaded")
        val sample = SampleCommon()
        val binding = FragmentFirstBinding.bind(view)
        binding.btnClickCount.setOnClickListener { sample.onClick() }
        binding.btnException.setOnClickListener { sample.logException() }
    }
}
