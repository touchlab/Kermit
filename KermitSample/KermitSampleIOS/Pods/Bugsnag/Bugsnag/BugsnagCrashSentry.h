//
//  BugsnagCrashSentry.h
//  Pods
//
//  Created by Jamie Lynch on 11/08/2017.
//
//

#import <Foundation/Foundation.h>

#import "BSG_KSCrashReportWriter.h"
#import "BSG_KSCrashType.h"

@class BugsnagConfiguration;
@class BugsnagErrorTypes;

@interface BugsnagCrashSentry : NSObject

- (void)install:(BugsnagConfiguration *)config onCrash:(BSGReportCallback)onCrash;

- (BSG_KSCrashType)mapKSToBSGCrashTypes:(BugsnagErrorTypes *)errorTypes;

@end
