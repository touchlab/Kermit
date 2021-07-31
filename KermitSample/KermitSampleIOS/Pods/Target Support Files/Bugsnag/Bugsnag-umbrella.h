#ifdef __OBJC__
#import <UIKit/UIKit.h>
#else
#ifndef FOUNDATION_EXPORT
#if defined(__cplusplus)
#define FOUNDATION_EXPORT extern "C"
#else
#define FOUNDATION_EXPORT extern
#endif
#endif
#endif

#import "BSG_KSCrashReportWriter.h"
#import "Bugsnag.h"
#import "BugsnagApp.h"
#import "BugsnagAppWithState.h"
#import "BugsnagBreadcrumb.h"
#import "BugsnagClient.h"
#import "BugsnagConfiguration.h"
#import "BugsnagDevice.h"
#import "BugsnagDeviceWithState.h"
#import "BugsnagEndpointConfiguration.h"
#import "BugsnagError.h"
#import "BugsnagErrorTypes.h"
#import "BugsnagEvent.h"
#import "BugsnagLastRunInfo.h"
#import "BugsnagMetadata.h"
#import "BugsnagMetadataStore.h"
#import "BugsnagPlugin.h"
#import "BugsnagSession.h"
#import "BugsnagStackframe.h"
#import "BugsnagThread.h"
#import "BugsnagUser.h"

FOUNDATION_EXPORT double BugsnagVersionNumber;
FOUNDATION_EXPORT const unsigned char BugsnagVersionString[];

