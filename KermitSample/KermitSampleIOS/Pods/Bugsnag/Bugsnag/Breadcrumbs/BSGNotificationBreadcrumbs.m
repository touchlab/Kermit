//
//  BSGNotificationBreadcrumbs.m
//  Bugsnag
//
//  Created by Nick Dowell on 10/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BSGNotificationBreadcrumbs.h"

#import "BugsnagBreadcrumbs.h"
#import "BugsnagConfiguration+Private.h"
#import "BugsnagKeys.h"

#if TARGET_OS_IOS || TARGET_OS_TV
#import "BSGUIKit.h"
#else
#import "BSGAppKit.h"
#endif


NSString * const BSGNotificationBreadcrumbsMessageAppWillTerminate = @"App Will Terminate";


@interface BSGNotificationBreadcrumbs ()

@property (nonatomic) NSDictionary<NSNotificationName, NSString *> *notificationNameMap;

@end


@implementation BSGNotificationBreadcrumbs

- (instancetype)initWithConfiguration:(BugsnagConfiguration *)configuration
                       breadcrumbSink:(id<BSGBreadcrumbSink>)breadcrumbSink {
    if ((self = [super init])) {
        _configuration = configuration;
        _notificationCenter = NSNotificationCenter.defaultCenter;
#if TARGET_OS_OSX
        _workspaceNotificationCenter = [NSWORKSPACE sharedWorkspace].notificationCenter;
#endif
        _breadcrumbSink = breadcrumbSink;
        _notificationNameMap = @{
#if TARGET_OS_TV
            NSUndoManagerDidRedoChangeNotification : @"Redo Operation",
            NSUndoManagerDidUndoChangeNotification : @"Undo Operation",
            UIScreenBrightnessDidChangeNotification : @"Screen Brightness Changed",
            UITableViewSelectionDidChangeNotification : @"TableView Select Change",
            UIWindowDidBecomeHiddenNotification : @"Window Became Hidden",
            UIWindowDidBecomeKeyNotification : @"Window Became Key",
            UIWindowDidBecomeVisibleNotification : @"Window Became Visible",
            UIWindowDidResignKeyNotification : @"Window Resigned Key",
#elif TARGET_OS_IOS
            NSUndoManagerDidRedoChangeNotification : @"Redo Operation",
            NSUndoManagerDidUndoChangeNotification : @"Undo Operation",
            UIApplicationDidEnterBackgroundNotification : @"App Did Enter Background",
            UIApplicationDidReceiveMemoryWarningNotification : @"Memory Warning",
            UIApplicationUserDidTakeScreenshotNotification : @"Took Screenshot",
            UIApplicationWillEnterForegroundNotification : @"App Will Enter Foreground",
            UIApplicationWillTerminateNotification : BSGNotificationBreadcrumbsMessageAppWillTerminate,
            UIDeviceBatteryLevelDidChangeNotification : @"Battery Level Changed",
            UIDeviceBatteryStateDidChangeNotification : @"Battery State Changed",
            UIDeviceOrientationDidChangeNotification : @"Orientation Changed",
            UIKeyboardDidHideNotification : @"Keyboard Became Hidden",
            UIKeyboardDidShowNotification : @"Keyboard Became Visible",
            UIMenuControllerDidHideMenuNotification : @"Did Hide Menu",
            UIMenuControllerDidShowMenuNotification : @"Did Show Menu",
            UITableViewSelectionDidChangeNotification : @"TableView Select Change",
            UITextFieldTextDidBeginEditingNotification : @"Began Editing Text",
            UITextFieldTextDidEndEditingNotification : @"Stopped Editing Text",
            UITextViewTextDidBeginEditingNotification : @"Began Editing Text",
            UITextViewTextDidEndEditingNotification : @"Stopped Editing Text",
            UIWindowDidBecomeHiddenNotification : @"Window Became Hidden",
            UIWindowDidBecomeVisibleNotification : @"Window Became Visible",
#elif TARGET_OS_OSX
            NSUndoManagerDidRedoChangeNotification : @"Redo Operation",
            NSUndoManagerDidUndoChangeNotification : @"Undo Operation",
            NSApplicationDidBecomeActiveNotification : @"App Became Active",
            NSApplicationDidHideNotification : @"App Did Hide",
            NSApplicationDidResignActiveNotification : @"App Resigned Active",
            NSApplicationDidUnhideNotification : @"App Did Unhide",
            NSApplicationWillTerminateNotification : BSGNotificationBreadcrumbsMessageAppWillTerminate,
            NSControlTextDidBeginEditingNotification : @"Control Text Began Edit",
            NSControlTextDidEndEditingNotification : @"Control Text Ended Edit",
            NSMenuWillSendActionNotification : @"Menu Will Send Action",
            NSTableViewSelectionDidChangeNotification : @"TableView Select Change",
            NSWindowDidBecomeKeyNotification : @"Window Became Key",
            NSWindowDidEnterFullScreenNotification : @"Window Entered Full Screen",
            NSWindowDidExitFullScreenNotification : @"Window Exited Full Screen",
            NSWindowWillCloseNotification : @"Window Will Close",
            NSWindowWillMiniaturizeNotification : @"Window Will Miniaturize",
            NSWorkspaceScreensDidSleepNotification : @"Workspace Screen Slept",
            NSWorkspaceScreensDidWakeNotification : @"Workspace Screen Awoke",
#endif
        };
    }
    return self;
}

#if TARGET_OS_OSX
- (NSArray<NSNotificationName> *)workspaceBreadcrumbStateEvents {
    return @[
        NSWorkspaceScreensDidSleepNotification,
        NSWorkspaceScreensDidWakeNotification
    ];
}
#endif

- (NSArray<NSNotificationName> *)automaticBreadcrumbStateEvents {
#if TARGET_OS_TV
    return @[
        NSUndoManagerDidRedoChangeNotification,
        NSUndoManagerDidUndoChangeNotification,
        UIScreenBrightnessDidChangeNotification,
        UIWindowDidBecomeHiddenNotification,
        UIWindowDidBecomeKeyNotification,
        UIWindowDidBecomeVisibleNotification,
        UIWindowDidResignKeyNotification,
    ];
#elif TARGET_OS_IOS
    return @[
        NSUndoManagerDidRedoChangeNotification,
        NSUndoManagerDidUndoChangeNotification,
        UIApplicationDidEnterBackgroundNotification,
        UIApplicationDidReceiveMemoryWarningNotification,
        UIApplicationUserDidTakeScreenshotNotification,
        UIApplicationWillEnterForegroundNotification,
        UIApplicationWillTerminateNotification,
        UIKeyboardDidHideNotification,
        UIKeyboardDidShowNotification,
        UIMenuControllerDidHideMenuNotification,
        UIMenuControllerDidShowMenuNotification,
        UIWindowDidBecomeHiddenNotification,
        UIWindowDidBecomeVisibleNotification,
    ];
#elif TARGET_OS_OSX
    return @[
        NSApplicationDidBecomeActiveNotification,
        NSApplicationDidResignActiveNotification,
        NSApplicationDidHideNotification,
        NSApplicationDidUnhideNotification,
        NSApplicationWillTerminateNotification,
        
        NSWindowDidBecomeKeyNotification,
        NSWindowDidEnterFullScreenNotification,
        NSWindowDidExitFullScreenNotification,
        NSWindowWillCloseNotification,
        NSWindowWillMiniaturizeNotification,
    ];
#endif
}

- (NSArray<NSNotificationName> *)automaticBreadcrumbControlEvents {
#if TARGET_OS_TV
    return nil;
#elif TARGET_OS_IOS
    return @[
        UITextFieldTextDidBeginEditingNotification,
        UITextFieldTextDidEndEditingNotification,
        UITextViewTextDidBeginEditingNotification,
        UITextViewTextDidEndEditingNotification
    ];
#elif TARGET_OS_OSX
    return @[
        NSControlTextDidBeginEditingNotification,
        NSControlTextDidEndEditingNotification
    ];
#endif
}

- (NSArray<NSNotificationName> *)automaticBreadcrumbTableItemEvents {
#if TARGET_OS_IOS || TARGET_OS_TV
    return @[ UITableViewSelectionDidChangeNotification ];
#elif TARGET_OS_OSX
    return @[ NSTableViewSelectionDidChangeNotification ];
#endif
}

- (NSArray<NSNotificationName> *)automaticBreadcrumbMenuItemEvents {
#if TARGET_OS_OSX
    return @[ NSMenuWillSendActionNotification ];
#endif
    return nil;
}

- (void)dealloc {
    [_notificationCenter removeObserver:self];
}

#pragma mark -

- (NSString *)messageForNotificationName:(NSNotificationName)name {
    return self.notificationNameMap[name] ?: [name stringByReplacingOccurrencesOfString:@"Notification" withString:@""];
}

- (void)addBreadcrumbWithType:(BSGBreadcrumbType)type forNotificationName:(NSNotificationName)notificationName {
    [self addBreadcrumbWithType:type forNotificationName:notificationName metadata:nil];
}

- (void)addBreadcrumbWithType:(BSGBreadcrumbType)type forNotificationName:(NSNotificationName)notificationName metadata:(NSDictionary *)metadata {
    [self.breadcrumbSink leaveBreadcrumbWithMessage:[self messageForNotificationName:notificationName] metadata:metadata ?: @{} andType:type];
}

#pragma mark -

- (void)start {
    // State events
    if ([self.configuration shouldRecordBreadcrumbType:BSGBreadcrumbTypeState]) {
        // Generic state events
        for (NSNotificationName name in [self automaticBreadcrumbStateEvents]) {
            [self startListeningForStateChangeNotification:name];
        }
        
#if TARGET_OS_OSX
        // Workspace-specific events - macOS only
        for (NSNotificationName name in [self workspaceBreadcrumbStateEvents]) {
            [self.workspaceNotificationCenter addObserver:self
                                             selector:@selector(addBreadcrumbForNotification:)
                                                 name:name
                                               object:nil];
        }
        
        // NSMenu events (macOS only)
        for (NSNotificationName name in [self automaticBreadcrumbMenuItemEvents]) {
            [self.notificationCenter addObserver:self
                                    selector:@selector(addBreadcrumbForMenuItemNotification:)
                                        name:name
                                      object:nil];
        }
#endif
    }
    
    // Navigation events
    if ([self.configuration shouldRecordBreadcrumbType:BSGBreadcrumbTypeNavigation]) {
        // UI/NSTableView events
        for (NSNotificationName name in [self automaticBreadcrumbTableItemEvents]) {
            [self.notificationCenter addObserver:self
                                    selector:@selector(addBreadcrumbForTableViewNotification:)
                                        name:name
                                      object:nil];
        }
    }
    
    // User events
    if ([self.configuration shouldRecordBreadcrumbType:BSGBreadcrumbTypeUser]) {
        // UITextField/NSControl events (text editing)
        for (NSNotificationName name in [self automaticBreadcrumbControlEvents]) {
            [self.notificationCenter addObserver:self
                                    selector:@selector(addBreadcrumbForControlNotification:)
                                        name:name
                                      object:nil];
        }
    }
}

- (void)startListeningForStateChangeNotification:(NSNotificationName)notificationName {
    [self.notificationCenter addObserver:self selector:@selector(addBreadcrumbForNotification:) name:notificationName object:nil];
}

- (void)addBreadcrumbForNotification:(NSNotification *)notification {
    [self addBreadcrumbWithType:BSGBreadcrumbTypeState forNotificationName:notification.name];
}

- (void)addBreadcrumbForTableViewNotification:(__attribute__((unused)) NSNotification *)notification {
#if TARGET_OS_IOS || TARGET_OS_TV
    NSIndexPath *indexPath = ((UITableView *)notification.object).indexPathForSelectedRow;
    [self addBreadcrumbWithType:BSGBreadcrumbTypeNavigation forNotificationName:notification.name metadata:
     indexPath ? @{@"row" : @(indexPath.row), @"section" : @(indexPath.section)} : nil];
#elif TARGET_OS_OSX
    NSTableView *tableView = notification.object;
    [self addBreadcrumbWithType:BSGBreadcrumbTypeNavigation forNotificationName:notification.name metadata:
     tableView ? @{@"selectedRow" : @(tableView.selectedRow), @"selectedColumn" : @(tableView.selectedColumn)} : nil];
#endif
}

- (void)addBreadcrumbForMenuItemNotification:(__attribute__((unused)) NSNotification *)notification {
#if TARGET_OS_OSX
    NSMenuItem *menuItem = [[notification userInfo] valueForKey:@"MenuItem"];
    [self addBreadcrumbWithType:BSGBreadcrumbTypeState forNotificationName:notification.name metadata:
     [menuItem isKindOfClass:NSMENUITEM] ? @{BSGKeyAction : menuItem.title} : nil];
#endif
}

- (void)addBreadcrumbForControlNotification:(__attribute__((unused)) NSNotification *)notification {
#if TARGET_OS_IOS
    NSString *label = ((UIControl *)notification.object).accessibilityLabel;
    [self addBreadcrumbWithType:BSGBreadcrumbTypeUser forNotificationName:notification.name metadata:
     label.length ? @{BSGKeyLabel : label} : nil];
#elif TARGET_OS_OSX
    NSControl *control = notification.object;
    NSDictionary *metadata = nil;
    if ([control respondsToSelector:@selector(accessibilityLabel)]) {
        NSString *label = control.accessibilityLabel;
        if (label.length > 0) {
            metadata = @{BSGKeyLabel : label};
        }
    }
    [self addBreadcrumbWithType:BSGBreadcrumbTypeUser forNotificationName:notification.name metadata:metadata];
#endif
}

@end
