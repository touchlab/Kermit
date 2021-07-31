//
//  BSGUIKit.h
//  Bugsnag
//
//  Created by Nick Dowell on 01/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

// When used in some memory constrained contexts such as a file provider extension, linking to UIKit is problematic.
// These macros exist to allow the use of UIKit without adding a link-time dependency on it.

// Calling code should be prepared for classes to not be found when UIKit is not linked.
#define UIAPPLICATION                                       NSClassFromString(@"UIApplication")
#define UIDEVICE                                            NSClassFromString(@"UIDevice")

#define UIApplicationDidBecomeActiveNotification            @"UIApplicationDidBecomeActiveNotification"
#define UIApplicationDidEnterBackgroundNotification         @"UIApplicationDidEnterBackgroundNotification"
#define UIApplicationDidReceiveMemoryWarningNotification    @"UIApplicationDidReceiveMemoryWarningNotification"
#define UIApplicationUserDidTakeScreenshotNotification      @"UIApplicationUserDidTakeScreenshotNotification"
#define UIApplicationWillEnterForegroundNotification        @"UIApplicationWillEnterForegroundNotification"
#define UIApplicationWillResignActiveNotification           @"UIApplicationWillResignActiveNotification"
#define UIApplicationWillTerminateNotification              @"UIApplicationWillTerminateNotification"
#define UIDeviceBatteryLevelDidChangeNotification           @"UIDeviceBatteryLevelDidChangeNotification"
#define UIDeviceBatteryStateDidChangeNotification           @"UIDeviceBatteryStateDidChangeNotification"
#define UIDeviceOrientationDidChangeNotification            @"UIDeviceOrientationDidChangeNotification"
#define UIKeyboardDidHideNotification                       @"UIKeyboardDidHideNotification"
#define UIKeyboardDidShowNotification                       @"UIKeyboardDidShowNotification"
#define UIMenuControllerDidHideMenuNotification             @"UIMenuControllerDidHideMenuNotification"
#define UIMenuControllerDidShowMenuNotification             @"UIMenuControllerDidShowMenuNotification"
#define UIScreenBrightnessDidChangeNotification             @"UIScreenBrightnessDidChangeNotification"
#define UITableViewSelectionDidChangeNotification           @"UITableViewSelectionDidChangeNotification"
#define UITextFieldTextDidBeginEditingNotification          @"UITextFieldTextDidBeginEditingNotification"
#define UITextFieldTextDidEndEditingNotification            @"UITextFieldTextDidEndEditingNotification"
#define UITextViewTextDidBeginEditingNotification           @"UITextViewTextDidBeginEditingNotification"
#define UITextViewTextDidEndEditingNotification             @"UITextViewTextDidEndEditingNotification"
#define UIWindowDidBecomeHiddenNotification                 @"UIWindowDidBecomeHiddenNotification"
#define UIWindowDidBecomeKeyNotification                    @"UIWindowDidBecomeKeyNotification"
#define UIWindowDidBecomeVisibleNotification                @"UIWindowDidBecomeVisibleNotification"
#define UIWindowDidResignKeyNotification                    @"UIWindowDidResignKeyNotification"
