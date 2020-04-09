//
//  ContentView.swift
//  KermitSampleIOS
//
//  Created by Samuel Hill on 4/9/20.
//  Copyright © 2020 Samuel Hill. All rights reserved.
//

import SwiftUI
import shared

struct ContentView: View {
    let kermit = UtilKt.getKermit()
    let common: SampleCommon
    
    init() {
        //TODO: tag doesn't display correctly
        self.common = SampleCommon(kermit: kermit)
        self.kermit.i(tag: "ContentView", throwable: nil, message: {"loaded"})
    }
    
    var body: some View {
        Button(action: {
            self.common.onClick()
        }){
            Text("Click Me").padding()
            .background(Color.blue)
            .foregroundColor(.white)
            .font(.title)
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
