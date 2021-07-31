//
//  BSGAppKit.h
//  Bugsnag
//
//  Created by Nick Dowell on 13/04/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import <AppKit/AppKit.h>

// Daemons and other processes running in non-UI sessions should not link against AppKit.
// These macros exist to allow the use of AppKit without adding a link-time dependency on it.

// Calling code should be prepared for classes to not be found when AppKit is not linked.
#define NSAPPLICATION                                       NSClassFromString(@"NSApplication")
#define NSMENUITEM                                          NSClassFromString(@"NSMenuItem")
#define NSWORKSPACE                                         NSClassFromString(@"NSWorkspace")

#define NSApplicationDidBecomeActiveNotification            @"NSApplicationDidBecomeActiveNotification"
#define NSApplicationDidBecomeActiveNotification            @"NSApplicationDidBecomeActiveNotification"
#define NSApplicationDidFinishLaunchingNotification         @"NSApplicationDidFinishLaunchingNotification"
#define NSApplicationDidHideNotification                    @"NSApplicationDidHideNotification"
#define NSApplicationDidResignActiveNotification            @"NSApplicationDidResignActiveNotification"
#define NSApplicationDidResignActiveNotification            @"NSApplicationDidResignActiveNotification"
#define NSApplicationDidUnhideNotification                  @"NSApplicationDidUnhideNotification"
#define NSApplicationWillBecomeActiveNotification           @"NSApplicationWillBecomeActiveNotification"
#define NSApplicationWillTerminateNotification              @"NSApplicationWillTerminateNotification"
#define NSApplicationWillTerminateNotification              @"NSApplicationWillTerminateNotification"
#define NSControlTextDidBeginEditingNotification            @"NSControlTextDidBeginEditingNotification"
#define NSControlTextDidEndEditingNotification              @"NSControlTextDidEndEditingNotification"
#define NSMenuWillSendActionNotification                    @"NSMenuWillSendActionNotification"
#define NSTableViewSelectionDidChangeNotification           @"NSTableViewSelectionDidChangeNotification"
#define NSUndoManagerDidRedoChangeNotification              @"NSUndoManagerDidRedoChangeNotification"
#define NSUndoManagerDidUndoChangeNotification              @"NSUndoManagerDidUndoChangeNotification"
#define NSWindowDidBecomeKeyNotification                    @"NSWindowDidBecomeKeyNotification"
#define NSWindowDidEnterFullScreenNotification              @"NSWindowDidEnterFullScreenNotification"
#define NSWindowDidExitFullScreenNotification               @"NSWindowDidExitFullScreenNotification"
#define NSWindowWillCloseNotification                       @"NSWindowWillCloseNotification"
#define NSWindowWillMiniaturizeNotification                 @"NSWindowWillMiniaturizeNotification"
#define NSWorkspaceScreensDidSleepNotification              @"NSWorkspaceScreensDidSleepNotification"
#define NSWorkspaceScreensDidWakeNotification               @"NSWorkspaceScreensDidWakeNotification"
