//
//  BSG_KSSystemInfo.h
//
//  Created by Karl Stenerud on 2012-02-05.
//
//  Copyright (c) 2012 Karl Stenerud. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall remain in place
// in this source code.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

#define BSG_KSSystemField_AppUUID "app_uuid"
#define BSG_KSSystemField_BinaryArch "binary_arch"
#define BSG_KSSystemField_BootTime "boot_time"
#define BSG_KSSystemField_BundleID "CFBundleIdentifier"
#define BSG_KSSystemField_BundleName "CFBundleName"
#define BSG_KSSystemField_BundleExecutable "CFBundleExecutable"
#define BSG_KSSystemField_BundleShortVersion "CFBundleShortVersionString"
#define BSG_KSSystemField_BundleVersion "CFBundleVersion"
#define BSG_KSSystemField_CPUArch "cpu_arch"
#define BSG_KSSystemField_DeviceAppHash "device_app_hash"
#define BSG_KSSystemField_Jailbroken "jailbroken"
#define BSG_KSSystemField_Machine "machine"
#define BSG_KSSystemField_Memory "memory"
#define BSG_KSSystemField_Model "model"
#define BSG_KSSystemField_OSVersion "os_version"
#define BSG_KSSystemField_Size "size"
#define BSG_KSSystemField_SystemName "system_name"
#define BSG_KSSystemField_SystemVersion "system_version"
#define BSG_KSSystemField_ClangVersion "clang_version"
#define BSG_KSSystemField_TimeZone "time_zone"
#define BSG_KSSystemField_Translated "proc_translated"
#define BSG_KSSystemField_iOSSupportVersion "iOSSupportVersion"

#import <Foundation/Foundation.h>
#import "BugsnagPlatformConditional.h"

#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
#import "BSGUIKit.h"
#endif

/**
 * Provides system information useful for a crash report.
 */
@interface BSG_KSSystemInfo : NSObject

/** Get the system info.
 *
 * @return The system info.
 */
+ (NSDictionary *)systemInfo;

/** Get this application's UUID.
 *
 * @return The UUID.
 */
+ (NSString *)appUUID;

/**
 * The build version of the OS
 */
+ (NSString *)osBuildVersion;

/**
 * Whether the current main bundle is an iOS app extension
 */
+ (BOOL)isRunningInAppExtension;

/** Generate a 20 byte SHA1 hash that remains unique across a single device and
 * application. This is slightly different from the Apple crash report key,
 * which is unique to the device, regardless of the application.
 *
 * @return The stringified hex representation of the hash for this device + app.
 */
+ (NSString *)deviceAndAppHash;

#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
+ (UIApplicationState)currentAppState;

/**
 * YES if the app is currently shown in the foreground
 */
+ (BOOL)isInForeground:(UIApplicationState)state;

#endif

@end
