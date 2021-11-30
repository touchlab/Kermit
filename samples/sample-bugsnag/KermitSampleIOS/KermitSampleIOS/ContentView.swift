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
    let cb = CrashBot()

    init() {
        LoggerKt.i {"Loaded"}
        self.common = SampleCommon(logger: Logger.companion)
    }

    var body: some View {
        VStack(spacing: 50){
            Button(action: {
                self.common.onClick()
            }){
                Text("Click Count").padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .font(.title)
            }
            Button(action: {
                self.common.logException()
            }){
                Text("Log Exception").padding()
                .background(Color.blue)
                .foregroundColor(.white)
                .font(.title)
            }
            Button(action: {
               self.cb.goCrash()
           }){
               Text("Kotlin Crash").padding()
               .background(Color.blue)
               .foregroundColor(.white)
               .font(.title)
           }
            
            Button(action: {
               realCrash()
           }){
               Text("Swift Crash").padding()
               .background(Color.blue)
               .foregroundColor(.white)
               .font(.title)
           }
        }
    }
}

func realCrash() {
    let numbers = [0]
    let _ = numbers[1]
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
