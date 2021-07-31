//
//  BSGEventUploadFileOperation.m
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadFileOperation.h"

#import "BSGFileLocations.h"
#import "BSGGlobals.h"
#import "BSGJSONSerialization.h"
#import "BugsnagEvent+Private.h"
#import "BugsnagLogger.h"


@implementation BSGEventUploadFileOperation

- (instancetype)initWithFile:(NSString *)file delegate:(id<BSGEventUploadOperationDelegate>)delegate {
    if ((self = [super initWithDelegate:delegate])) {
        _file = [file copy];
    }
    return self;
}

- (BugsnagEvent *)loadEventAndReturnError:(NSError * __autoreleasing *)errorPtr {
    id json = [BSGJSONSerialization JSONObjectWithContentsOfFile:self.file options:0 error:errorPtr];
    if (![json isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    return [[BugsnagEvent alloc] initWithJson:json];
}

- (void)deleteEvent {
    dispatch_sync(BSGGlobalsFileSystemQueue(), ^{
        NSError *error = nil;
        if ([NSFileManager.defaultManager removeItemAtPath:self.file error:&error]) {
            bsg_log_debug(@"Deleted event %@", self.name);
        } else {
            bsg_log_err(@"%@", error);
        }
    });
}

- (void)storeEventPayload:(__attribute__((unused)) NSDictionary *)eventPayload {
    // This event was loaded from disk, so nothing needs to be saved.
}

- (NSString *)name {
    return self.file.lastPathComponent;
}

@end
