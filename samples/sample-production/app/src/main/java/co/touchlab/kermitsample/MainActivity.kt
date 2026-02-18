/*
 * Copyright (c) 2020 Touchlab
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package co.touchlab.kermitsample

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Logger.withTag("MainActivity").v("Main activity loaded")

        val sample = SampleCommon()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 64.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        SampleButton(text = "Click Count V", onClick = { sample.onClickV() })
                        SampleButton(text = "Click Count D", onClick = { sample.onClickD() })
                        SampleButton(text = "Click Count I", onClick = { sample.onClickI() })
                        SampleButton(text = "Click Count W", onClick = { sample.onClickW() })
                        SampleButton(text = "Click Count E", onClick = { sample.onClickE() })
                        SampleButton(text = "Click Count A", onClick = { sample.onClickA() })
                        SampleButton(text = "Log Exception", onClick = { sample.logException() })
                        SampleButton(text = "Log from Android", onClick = { Logger.i { "Hello from Android" } })
                    }
                }
            }
        }
    }

    @Composable
    private fun SampleButton(text: String, onClick: () -> Unit) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White,
            ),
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
