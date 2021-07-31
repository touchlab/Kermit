//
//  BSGEventUploadKSCrashReportOperation.h
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadFileOperation.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * A concrete operation class for reading a KSCrashReport from disk, converting it into a BugsnagEvent, and uploading.
 */
@interface BSGEventUploadKSCrashReportOperation : BSGEventUploadFileOperation

@end

NS_ASSUME_NONNULL_END
