//
//  BugsnagPlatformConditional.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/06/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

// ***IMPORTANT NOTE***: this should always be imported as the first header in a file,
// because it relies on preprocessor macros. If this is not done the targets will
// not be defined appropriately and the functions/defines will behave unexpectedly.

#ifndef BugsnagPlatformConditional_h
#define BugsnagPlatformConditional_h

#include <TargetConditionals.h>

/**
 * Defined as true if this is the iOS platform.
 */
#define BSG_PLATFORM_IOS TARGET_OS_IOS

/**
* Defined as true if this is the OSX platform.
*/
#define BSG_PLATFORM_OSX TARGET_OS_OSX

/**
* Defined as true if this is the tvOS platform.
*/
#define BSG_PLATFORM_TVOS TARGET_OS_TV

/**
* Defined as true if this is the watchOS platform.
*/
#define BSG_PLATFORM_WATCHOS TARGET_OS_WATCH

/**
* Defined as true if this is a simulator.
*/
#define BSG_PLATFORM_SIMULATOR TARGET_OS_SIMULATOR

#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
#define BSG_HAS_UIKIT 1
#else
#define BSG_HAS_UIKIT 0
#endif

#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
#define BSG_HAS_UIDEVICE 1
#else
#define BSG_HAS_UIDEVICE 0
#endif

#if BSG_PLATFORM_IOS || BSG_PLATFORM_OSX
#define BSG_HAS_MACH 1
#else
#define BSG_HAS_MACH 0
#endif


#endif /* BugsnagPlatformConditional_h */
