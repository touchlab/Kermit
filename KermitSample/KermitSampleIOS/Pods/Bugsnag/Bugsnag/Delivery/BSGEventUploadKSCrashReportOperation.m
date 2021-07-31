//
//  BSGEventUploadKSCrashReportOperation.m
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadKSCrashReportOperation.h"

#import "BSGInternalErrorReporter.h"
#import "BSGJSONSerialization.h"
#import "BSG_KSCrashDoctor.h"
#import "BSG_KSCrashReportFields.h"
#import "BSG_RFC3339DateTool.h"
#import "BugsnagAppWithState.h"
#import "BugsnagCollections.h"
#import "BugsnagConfiguration.h"
#import "BugsnagEvent+Private.h"
#import "BugsnagLogger.h"


static void ReportInternalError(NSString *errorClass, NSString *message, NSDictionary *diagnostics) {
    //
    // NSJSONSerialization deserialization errors unhelpfully all have the same error domain & code - NSCocoaErrorDomain 3840.
    // Therefore it's more useful to group based on the error description - but some of the descriptions contain character
    // offsets which would lead to some types of errors not being grouped at all; e.g.
    // - "Invalid value around character 229194."
    // - "No string key for value in object around character 94208."
    // - "Unable to convert data to string around character 158259."
    // - "Unterminated string around character 22568."
    //
    NSString *groupingMessage = [message componentsSeparatedByString:@" around character "].firstObject;
    NSString *groupingHash = [NSString stringWithFormat:@"BSGEventUploadKSCrashReportOperation.m: %@: %@", errorClass, groupingMessage];
    [BSGInternalErrorReporter.sharedInstance reportErrorWithClass:errorClass message:message diagnostics:diagnostics groupingHash:groupingHash];
}


@implementation BSGEventUploadKSCrashReportOperation

- (BugsnagEvent *)loadEventAndReturnError:(NSError * __autoreleasing *)errorPtr {
    NSError *error = nil;
    
    NSData *data = [NSData dataWithContentsOfFile:self.file options:0 error:&error];
    if (!data) {
        ReportInternalError(@"File reading error", BSGErrorDescription(error), error.userInfo);
        if (errorPtr) {
            *errorPtr = error;
        }
        return nil;
    }
    
    id json = [BSGJSONSerialization JSONObjectWithData:data options:0 error:&error];
    if (!json) {
        NSMutableDictionary *diagnostics = [NSMutableDictionary dictionary];
        diagnostics[@"data"] = [data base64EncodedStringWithOptions:0];
        ReportInternalError(@"JSON parsing error", BSGErrorDescription(error), diagnostics);
        if (errorPtr) {
            *errorPtr = error;
        }
        return nil;
    }
    
    NSDictionary *crashReport = [self fixupCrashReport:json];
    if (!crashReport) {
        ReportInternalError(@"Unexpected JSON payload", @"-fixupCrashReport: returned nil", @{@"json": json});
        return nil;
    }
    
    BugsnagEvent *event = [[BugsnagEvent alloc] initWithKSReport:crashReport];
    
    if (!event.app.type) {
        // Use current value for crashes from older notifier versions that didn't persist config.appType
        event.app.type = self.delegate.configuration.appType;
    }
    
    return event;
}

// Methods below were copied from BSG_KSCrashReportStore.m

- (NSMutableDictionary *)fixupCrashReport:(NSDictionary *)report {
    if (![report isKindOfClass:[NSDictionary class]]) {
        bsg_log_err(@"Report should be a dictionary, not %@", [report class]);
        return nil;
    }

    NSMutableDictionary *mutableReport = [report mutableCopy];
    NSMutableDictionary *mutableInfo =
            [report[@BSG_KSCrashField_Report] mutableCopy];
    mutableReport[@BSG_KSCrashField_Report] = mutableInfo;

    // Timestamp gets stored as a unix timestamp. Convert it to rfc3339.
    NSNumber *timestampMillis = mutableInfo[@BSG_KSCrashField_Timestamp_Millis];
    if ([timestampMillis isKindOfClass:[NSNumber class]]) {
        NSTimeInterval timeInterval = (double)timestampMillis.unsignedLongLongValue / 1000.0;
        NSDate *date = [NSDate dateWithTimeIntervalSince1970:timeInterval];
        mutableInfo[@BSG_KSCrashField_Timestamp] = [BSG_RFC3339DateTool stringFromDate:date];
    } else {
        [self convertTimestamp:@BSG_KSCrashField_Timestamp inReport:mutableInfo];
    }

    [self mergeDictWithKey:@BSG_KSCrashField_SystemAtCrash
           intoDictWithKey:@BSG_KSCrashField_System
                  inReport:mutableReport];

    [self mergeDictWithKey:@BSG_KSCrashField_UserAtCrash
           intoDictWithKey:@BSG_KSCrashField_User
                  inReport:mutableReport];

    NSMutableDictionary *crashReport =
            [report[@BSG_KSCrashField_Crash] mutableCopy];
    mutableReport[@BSG_KSCrashField_Crash] = crashReport;
    BSG_KSCrashDoctor *doctor = [BSG_KSCrashDoctor doctor];
    crashReport[@BSG_KSCrashField_Diagnosis] = [doctor diagnoseCrash:report];

    return mutableReport;
}

- (void)mergeDictWithKey:(NSString *)srcKey
         intoDictWithKey:(NSString *)dstKey
                inReport:(NSMutableDictionary *)report {
    NSDictionary *srcDict = report[srcKey];
    if (srcDict == nil) {
        // It's OK if the source dict didn't exist.
        return;
    }

    NSDictionary *dstDict = report[dstKey];
    if (dstDict == nil) {
        dstDict = @{};
    }
    if (![dstDict isKindOfClass:[NSDictionary class]]) {
        bsg_log_err(@"'%@' should be a dictionary, not %@", dstKey,
                [dstDict class]);
        return;
    }

    report[dstKey] = BSGDictMerge(srcDict, dstDict);
    [report removeObjectForKey:srcKey];
}

- (void)convertTimestamp:(NSString *)key
                inReport:(NSMutableDictionary *)report {
    NSNumber *timestamp = report[key];
    if (timestamp == nil) {
        bsg_log_err(@"entry '%@' not found", key);
        return;
    }
    [report
            setValue:[BSG_RFC3339DateTool
                    stringFromUNIXTimestamp:[timestamp doubleValue]]
              forKey:key];
}

@end
