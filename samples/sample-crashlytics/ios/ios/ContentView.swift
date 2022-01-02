//
//  ContentView.swift
//  ios
//
//  Created by Kevin Galligan on 12/29/21.
//

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