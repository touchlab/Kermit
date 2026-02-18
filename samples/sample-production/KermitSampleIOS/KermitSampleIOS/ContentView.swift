//
//  ContentView.swift
//  KermitSampleIOS

// Copyright (c) 2020 Touchlab
// Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

import SwiftUI
import shared

struct ContentView: View {

    let common: SampleCommon

    init() {
        self.common = SampleCommon()
    }

    var body: some View {
        VStack(spacing: 12) {
            SampleButton(text: "Click Count V", onClick: {
                self.common.onClickV()
            })

            SampleButton(text: "Click Count D", onClick: {
                self.common.onClickD()
            })

            SampleButton(text: "Click Count I", onClick: {
                self.common.onClickI()
            })

            SampleButton(text: "Click Count W", onClick: {
                self.common.onClickW()
            })

            SampleButton(text: "Click Count E", onClick: {
                self.common.onClickE()
            })

            SampleButton(text: "Click Count A", onClick: {
                self.common.onClickA()
            })

            SampleButton(text: "Log Exception", onClick: {
                self.common.logException()
            })

            SampleButton(text: "Log from iOS", onClick: {
                self.common.logException()
            })
        }
        .padding(.horizontal, 64)
        .frame(maxHeight: .infinity)
    }

    private func SampleButton(text: String, onClick: @escaping () -> Void) -> some View {
        Button(action: onClick) {
            Text(text)
                .font(.headline)
                .foregroundColor(.white)
                .frame(maxWidth: .infinity)
                .padding(.vertical, 12)
        }
        .background(Color.blue)
        .cornerRadius(32)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
