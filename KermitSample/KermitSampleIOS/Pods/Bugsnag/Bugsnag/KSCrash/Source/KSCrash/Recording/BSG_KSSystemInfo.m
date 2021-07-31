//
//  KSSystemInfo.m
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

#import "BugsnagPlatformConditional.h"

#import "BSG_KSSystemInfo.h"
#import "BSG_KSSystemInfoC.h"
#import "BSG_KSDynamicLinker.h"
#import "BSG_KSMachHeaders.h"
#import "BSG_KSJSONCodecObjC.h"
#import "BSG_KSMach.h"
#import "BSG_KSSysCtl.h"
#import "BugsnagKeys.h"
#import "BugsnagCollections.h"
#import "BSG_KSLogger.h"
#import "BSG_KSCrashReportFields.h"
#import "BSG_KSMach.h"
#import "BSG_KSCrash.h"

#import <CommonCrypto/CommonDigest.h>
#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
#import "BSGUIKit.h"
#endif
#import "BSG_Jailbreak.h"


static inline bool is_jailbroken() {
    static bool initialized_jb;
    static bool is_jb;
    if(!initialized_jb) {
        get_jailbreak_status(&is_jb);

        // Also keep using the old detection method.
        if(bsg_mach_headers_image_named("MobileSubstrate", false) != NULL) {
            is_jb = true;
        }
        initialized_jb = true;
    }

    return is_jb;
}

/**
 * Returns the content of /System/Library/CoreServices/SystemVersion.plist
 * bypassing the open syscall shim that would normally redirect access to this
 * file for iOS apps running on macOS.
 *
 * https://opensource.apple.com/source/xnu/xnu-7195.81.3/libsyscall/wrappers/system-version-compat.c.auto.html
 */
#if !BSG_PLATFORM_SIMULATOR
static NSDictionary * bsg_systemversion() {
    int fd = -1;
    char buffer[1024] = {0};
    const char *file = "/System/Library/CoreServices/SystemVersion.plist";
    bsg_syscall_open(file, O_RDONLY, 0, &fd);
    if (fd < 0) {
        BSG_KSLOG_ERROR("Could not open SystemVersion.plist");
        return nil;
    }
    ssize_t length = read(fd, buffer, sizeof(buffer));
    close(fd);
    if (length < 0 || length == sizeof(buffer)) {
        BSG_KSLOG_ERROR("Could not read SystemVersion.plist");
        return nil;
    }
    NSData *data = [NSData
                    dataWithBytesNoCopy:buffer
                    length:(NSUInteger)length freeWhenDone:NO];
    if (!data) {
        BSG_KSLOG_ERROR("Could not read SystemVersion.plist");
        return nil;
    }
    NSError *error = nil;
    NSDictionary *systemVersion = [NSPropertyListSerialization
                                   propertyListWithData:data
                                   options:0 format:NULL error:&error];
    if (!systemVersion) {
        BSG_KSLOG_ERROR("Could not read SystemVersion.plist: %@", error);
    }
    return systemVersion;
}
#endif

@implementation BSG_KSSystemInfo

// ============================================================================
#pragma mark - Utility -
// ============================================================================

/** Get a sysctl value as an NSNumber.
 *
 * @param name The sysctl name.
 *
 * @return The result of the sysctl call.
 */
+ (NSNumber *)int32Sysctl:(NSString *)name {
    return @(bsg_kssysctl_int32ForName(
            [name cStringUsingEncoding:NSUTF8StringEncoding]));
}

/** Get a sysctl value as an NSNumber.
 *
 * @param name The sysctl name.
 *
 * @return The result of the sysctl call.
 */
+ (NSNumber *)int64Sysctl:(NSString *)name {
    return @(bsg_kssysctl_int64ForName([name
            cStringUsingEncoding:NSUTF8StringEncoding]));
}

/** Get a sysctl value as an NSString.
 *
 * @param name The sysctl name.
 *
 * @return The result of the sysctl call.
 */
+ (NSString *)stringSysctl:(NSString *)name {
    NSString *str = nil;
    size_t size = bsg_kssysctl_stringForName(
        [name cStringUsingEncoding:NSUTF8StringEncoding], NULL, 0);

    if (size <= 0) {
        return @"";
    }

    NSMutableData *value = [NSMutableData dataWithLength:size];

    if (bsg_kssysctl_stringForName(
            [name cStringUsingEncoding:NSUTF8StringEncoding],
            value.mutableBytes, size) != 0) {
        str = [NSString stringWithCString:value.mutableBytes
                                 encoding:NSUTF8StringEncoding];
    }

    return str;
}

/** Get a sysctl value as an NSDate.
 *
 * @param name The sysctl name.
 *
 * @return The result of the sysctl call.
 */
+ (NSDate *)dateSysctl:(NSString *)name {
    NSDate *result = nil;

    struct timeval value = bsg_kssysctl_timevalForName(
        [name cStringUsingEncoding:NSUTF8StringEncoding]);
    if (!(value.tv_sec == 0 && value.tv_usec == 0)) {
        result =
            [NSDate dateWithTimeIntervalSince1970:(NSTimeInterval)value.tv_sec];
    }

    return result;
}

/** Convert raw UUID bytes to a human-readable string.
 *
 * @param uuidBytes The UUID bytes (must be 16 bytes long).
 *
 * @return The human readable form of the UUID.
 */
+ (NSString *)uuidBytesToString:(const uint8_t *)uuidBytes {
    CFUUIDRef uuidRef =
        CFUUIDCreateFromUUIDBytes(NULL, *((const CFUUIDBytes *)uuidBytes));
    NSString *str =
        (__bridge_transfer NSString *)CFUUIDCreateString(NULL, uuidRef);
    CFRelease(uuidRef);

    return str;
}

/** Get this application's UUID.
 *
 * @return The UUID.
 */
+ (NSString *)appUUID {
    BSG_Mach_Header_Info *image = bsg_mach_headers_get_main_image();
    return (image && image->uuid) ? [self uuidBytesToString:image->uuid] : nil;
}

+ (NSString *)deviceAndAppHash {
    NSMutableData *data = nil;

#if BSG_HAS_UIDEVICE
    if ([[UIDEVICE currentDevice]
            respondsToSelector:@selector(identifierForVendor)]) {
        data = [NSMutableData dataWithLength:16];
        [[UIDEVICE currentDevice].identifierForVendor
            getUUIDBytes:data.mutableBytes];
    } else
#endif
    {
        data = [NSMutableData dataWithLength:6];
        bsg_kssysctl_getMacAddress(BSGKeyDefaultMacName, [data mutableBytes]);
    }

    // Append some device-specific data.
    [data appendData:(NSData * _Nonnull)[[self stringSysctl:@"hw.machine"]
                         dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:(NSData * _Nonnull)[[self stringSysctl:@"hw.model"]
                         dataUsingEncoding:NSUTF8StringEncoding]];
    [data appendData:(NSData * _Nonnull)[[self currentCPUArch]
                         dataUsingEncoding:NSUTF8StringEncoding]];

    // Append the bundle ID.
    NSData *bundleID = [[[NSBundle mainBundle] bundleIdentifier]
        dataUsingEncoding:NSUTF8StringEncoding];
    if (bundleID != nil) {
        [data appendData:bundleID];
    }

    // SHA the whole thing.
    uint8_t sha[CC_SHA1_DIGEST_LENGTH];
    CC_SHA1([data bytes], (CC_LONG)[data length], sha);

    NSMutableString *hash = [NSMutableString string];
    for (size_t i = 0; i < sizeof(sha); i++) {
        [hash appendFormat:@"%02x", sha[i]];
    }

    return hash;
}

/** Get the current CPU's architecture.
 *
 * @return The current CPU archutecture.
 */
+ (NSString *)CPUArchForCPUType:(cpu_type_t)cpuType
                        subType:(cpu_subtype_t)subType {
    switch (cpuType) {
    case CPU_TYPE_ARM: {
        switch (subType) {
        case CPU_SUBTYPE_ARM_V6:
            return @"armv6";
        case CPU_SUBTYPE_ARM_V7:
            return @"armv7";
        case CPU_SUBTYPE_ARM_V7F:
            return @"armv7f";
        case CPU_SUBTYPE_ARM_V7K:
            return @"armv7k";
#ifdef CPU_SUBTYPE_ARM_V7S
        case CPU_SUBTYPE_ARM_V7S:
            return @"armv7s";
#endif
        case CPU_SUBTYPE_ARM_V8:
            return @"armv8";
        }
        break;
    }
    case CPU_TYPE_ARM64: {
        switch (subType) {
        case CPU_SUBTYPE_ARM64E:
            return @"arm64e";
        default:
            return @"arm64";
        }
    }
    case CPU_TYPE_X86:
        return @"x86";
    case CPU_TYPE_X86_64:
        return @"x86_64";
    }

    return nil;
}

+ (NSString *)currentCPUArch {
    NSString *result =
        [self CPUArchForCPUType:bsg_kssysctl_int32ForName(BSGKeyHwCputype)
                        subType:bsg_kssysctl_int32ForName(BSGKeyHwCpusubtype)];

    return result ?: [NSString stringWithUTF8String:bsg_ksmachcurrentCPUArch()];
}

// ============================================================================
#pragma mark - API -
// ============================================================================

/**
 * Returns a systemInfo dictionary containing all the nonvolatile unchanging values.
 */
+ (NSDictionary *)systemInfoStatic {
    static NSDictionary *sysInfo;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sysInfo = [self buildSystemInfoStatic];
    });
    return sysInfo;
}

+ (NSDictionary *)buildSystemInfoStatic {
    NSMutableDictionary *sysInfo = [NSMutableDictionary dictionary];

    NSBundle *mainBundle = [NSBundle mainBundle];
    NSDictionary *infoDict = [mainBundle infoDictionary];
    const struct mach_header *header = _dyld_get_image_header(0);
#ifdef __clang_version__
    sysInfo[@BSG_KSSystemField_ClangVersion] = @__clang_version__;
#endif

#if TARGET_OS_SIMULATOR
    //
    // When running on the simulator, we want to report the name and version of
    // the simlated OS.
    //

#if TARGET_OS_IOS
    // Note: This does not match UIDevice.currentDevice.systemName for versions
    // prior to (and some versions of) iOS 9 where the systemName was reported
    // as "iPhone OS". UIDevice gets its data from MobileGestalt which is a
    // private API. /System/Library/CoreServices/SystemVersion.plist contains
    // the information we need but will contain the macOS information when
    // running on the Simulator.
    sysInfo[@BSG_KSSystemField_SystemName] = @"iOS";
#elif TARGET_OS_TV
    sysInfo[@BSG_KSSystemField_SystemName] = @"tvOS";
#endif // TARGET_OS_IOS

    NSDictionary *env = NSProcessInfo.processInfo.environment;
    sysInfo[@BSG_KSSystemField_SystemVersion] = env[@"SIMULATOR_RUNTIME_VERSION"];
    sysInfo[@BSG_KSSystemField_Machine] = env[@"SIMULATOR_MODEL_IDENTIFIER"];
    sysInfo[@BSG_KSSystemField_Model] = @"simulator";

#else // !TARGET_OS_SIMULATOR

    //
    // Report the name and version of the underlying OS the app is running on.
    // For Mac Catalyst and iOS apps running on macOS, this means macOS rather
    // than the version of iOS it emulates ("iOSSupportVersion")
    //
    NSDictionary *sysVersion = bsg_systemversion();

#if TARGET_OS_IOS || TARGET_OS_OSX
    NSString *systemName = sysVersion[@"ProductName"];
    if ([systemName isEqual:@"iPhone OS"]) {
        systemName = @"iOS";
    } else if
        // "ProductName" changed from "Mac OS X" to "macOS" in 11.0
        ([systemName isEqual:@"macOS"] || [systemName isEqual:@"Mac OS X"]) {
        // KSCrash had the name hard-coded this way when we forked it.
        systemName = @"Mac OS";
    }
#elif TARGET_OS_TV
    NSString *systemName = @"tvOS";
#endif

    sysInfo[@BSG_KSSystemField_SystemName] = systemName;
    sysInfo[@BSG_KSSystemField_SystemVersion] = sysVersion[@"ProductVersion"];

#if TARGET_OS_IOS
    sysInfo[@BSG_KSSystemField_iOSSupportVersion] = sysVersion[@"iOSSupportVersion"];
#endif

    // Bugsnag payload mapping:
    //
    // BSG_KSSystemField_Machine => device.model
    // BSG_KSSystemField_Model   => device.modelNumber

    if ([systemName isEqual:@"Mac OS"]) {
        // On macOS hw.model contains the "Model Identifier" e.g. MacBookPro16,1
        sysInfo[@BSG_KSSystemField_Machine] = [self stringSysctl:@"hw.model"];
        // and hw.machine contains the instruction set - e.g. "arm64" or "x86_64"
        // we omit this since it doesn't match what we're expecting or want.
    } else {
        // On iOS & tvOS hw.machine contains the "Model Identifier" or
        // "ProductType" - e.g. "iPhone6,1"
        sysInfo[@BSG_KSSystemField_Machine] = [self stringSysctl:@"hw.machine"];
        // and hw.model contains the "Internal Name" or "Board ID" - e.g. "D79AP"
        sysInfo[@BSG_KSSystemField_Model] = [self stringSysctl:@"hw.model"];
    }

#endif // TARGET_OS_SIMULATOR

    sysInfo[@BSG_KSSystemField_OSVersion] = [self osBuildVersion];
    sysInfo[@BSG_KSSystemField_BootTime] = [self dateSysctl:@"kern.boottime"];
    sysInfo[@BSG_KSSystemField_BundleID] = infoDict[@"CFBundleIdentifier"];
    sysInfo[@BSG_KSSystemField_BundleName] = infoDict[@"CFBundleName"];
    sysInfo[@BSG_KSSystemField_BundleExecutable] = infoDict[@"CFBundleExecutable"];
    sysInfo[@BSG_KSSystemField_BundleVersion] = infoDict[@"CFBundleVersion"];
    sysInfo[@BSG_KSSystemField_BundleShortVersion] = infoDict[@"CFBundleShortVersionString"];
    sysInfo[@BSG_KSSystemField_AppUUID] = [self appUUID];
    sysInfo[@BSG_KSSystemField_CPUArch] = [self currentCPUArch];
    sysInfo[@BSG_KSSystemField_BinaryArch] = [self CPUArchForCPUType:header->cputype subType:header->cpusubtype];
    sysInfo[@BSG_KSSystemField_DeviceAppHash] = [self deviceAndAppHash];

#if TARGET_OS_OSX || TARGET_OS_MACCATALYST
    // https://developer.apple.com/documentation/apple-silicon/about-the-rosetta-translation-environment
    int proc_translated = 0;
    size_t size = sizeof(proc_translated);
    if (!sysctlbyname("sysctl.proc_translated", &proc_translated, &size, NULL, 0) && proc_translated) {
        sysInfo[@BSG_KSSystemField_Translated] = @YES;
    }
#endif

    return sysInfo;
}

+ (NSDictionary *)systemInfo {
    NSMutableDictionary *sysInfo = [[self systemInfoStatic] mutableCopy];

    sysInfo[@BSG_KSSystemField_Jailbroken] = @(is_jailbroken());
    sysInfo[@BSG_KSSystemField_TimeZone] = [[NSTimeZone localTimeZone] abbreviation];
    sysInfo[@BSG_KSSystemField_Memory] = @{
        @BSG_KSCrashField_Free: @(bsg_ksmachfreeMemory()),
        @BSG_KSCrashField_Usable: @(bsg_ksmachusableMemory()),
        @BSG_KSSystemField_Size: [self int64Sysctl:@"hw.memsize"]
    };

    NSDictionary *statsInfo = [[BSG_KSCrash sharedInstance] captureAppStats];
    sysInfo[@BSG_KSCrashField_AppStats] = statsInfo;
    return sysInfo;
}

+ (NSString *)osBuildVersion {
    return [self stringSysctl:@"kern.osversion"];
}

+ (BOOL)isRunningInAppExtension {
#if BSG_PLATFORM_IOS
    NSBundle *mainBundle = [NSBundle mainBundle];
    // From the App Extension Programming Guide:
    // > When you build an extension based on an Xcode template, you get an
    // > extension bundle that ends in .appex.
    return [[mainBundle executablePath] containsString:@".appex"]
        // In the case that the extension bundle was renamed or generated
        // outside of the Xcode template, check the Bundle OS Type Code:
        // > This key consists of a four-letter code for the bundle type. For
        // > apps, the code is APPL, for frameworks, it's FMWK, and for bundles,
        // > it's BNDL.
        // If the main bundle type is not "APPL", assume this is an extension
        // context.
        || ![[mainBundle infoDictionary][@"CFBundlePackageType"] isEqualToString:@"APPL"];
#else
    return NO;
#endif
}

#if BSG_PLATFORM_IOS || BSG_PLATFORM_TVOS
+ (UIApplicationState)currentAppState {
    // Only checked outside of app extensions since sharedApplication is
    // unavailable to extension UIKit APIs
    if ([self isRunningInAppExtension]) {
        return UIApplicationStateActive;
    }

    UIApplicationState(^getState)(void) = ^() {
        // Calling this API indirectly to avoid a compile-time check that
        // [UIApplication sharedApplication] is not called from app extensions
        // (which is handled above)
        UIApplication *app = [UIAPPLICATION performSelector:@selector(sharedApplication)];
        return [app applicationState];
    };

    if ([[NSThread currentThread] isMainThread]) {
        return getState();
    } else {
        // [UIApplication sharedApplication] is a main thread-only API
        __block UIApplicationState state;
        dispatch_sync(dispatch_get_main_queue(), ^{
            state = getState();
        });
        return state;
    }
}

+ (BOOL)isInForeground:(UIApplicationState)state {
    // The app is in the foreground if the current state is "active" or
    // "inactive". From the UIApplicationState docs:
    // > UIApplicationStateActive
    // >   The app is running in the foreground and currently receiving events.
    // > UIApplicationStateInactive
    // >   The app is running in the foreground but is not receiving events.
    // >   This might happen as a result of an interruption or because the app
    // >   is transitioning to or from the background.
    // > UIApplicationStateBackground
    // >   The app is running in the background.
    return state == UIApplicationStateInactive
        || state == UIApplicationStateActive;
}
#endif

@end

char *bsg_kssysteminfo_toJSON(void) {
    NSError *error;
    NSMutableDictionary *systemInfo = [[BSG_KSSystemInfo systemInfo] mutableCopy];

    // Make sure the jailbroken status didn't get patched out.
    systemInfo[@BSG_KSSystemField_Jailbroken] = @(is_jailbroken());

    NSMutableData *jsonData =
        (NSMutableData *)[BSG_KSJSONCodec encode:systemInfo
                                         options:BSG_KSJSONEncodeOptionSorted
                                           error:&error];
    if (error != nil) {
        BSG_KSLOG_ERROR(@"Could not serialize system info: %@", error);
        return NULL;
    }
    if (![jsonData isKindOfClass:[NSMutableData class]]) {
        jsonData = [NSMutableData dataWithData:jsonData];
    }

    [jsonData appendBytes:"\0" length:1];
    return strdup([jsonData bytes]);
}

char *bsg_kssysteminfo_copyProcessName(void) {
    return strdup([[NSProcessInfo processInfo].processName UTF8String]);
}
