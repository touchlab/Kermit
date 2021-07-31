//
//  BugsnagBreadcrumbs.m
//  Bugsnag
//
//  Created by Jamie Lynch on 26/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "BugsnagBreadcrumbs.h"

#import "BSGGlobals.h"
#import "BSGFileLocations.h"
#import "BSGJSONSerialization.h"
#import "BSG_KSCrashReportWriter.h"
#import "BSG_RFC3339DateTool.h"
#import "BugsnagBreadcrumb+Private.h"
#import "BugsnagCollections.h"
#import "BugsnagConfiguration+Private.h"
#import "BugsnagLogger.h"


//
// Breadcrumbs are stored as a linked list of JSON encoded C strings
// so that they are accessible at crash time.
//

struct bsg_breadcrumb_list_item {
    struct bsg_breadcrumb_list_item *next;
    char jsonData[]; // MUST be null terminated
};

static struct bsg_breadcrumb_list_item *g_breadcrumbs_head;

#pragma mark -

@interface BugsnagBreadcrumbs ()

@property (readonly, nonatomic) NSString *breadcrumbsPath;

@property (nonatomic) BugsnagConfiguration *config;
@property (nonatomic) unsigned int nextFileNumber;
@property (nonatomic) unsigned int maxBreadcrumbs;

@end

#pragma mark -

@implementation BugsnagBreadcrumbs

- (instancetype)initWithConfiguration:(BugsnagConfiguration *)config {
    if (!(self = [super init])) {
        return nil;
    }
    
    _config = config;
    // Capture maxBreadcrumbs to protect against config being changed after initialization
    _maxBreadcrumbs = (unsigned int)config.maxBreadcrumbs;
    
    _breadcrumbsPath = [BSGFileLocations current].breadcrumbs;
    
    return self;
}

- (NSArray<BugsnagBreadcrumb *> *)breadcrumbs {
    NSMutableArray<BugsnagBreadcrumb *> *breadcrumbs = [NSMutableArray array];
    @synchronized (self) {
        for (struct bsg_breadcrumb_list_item *item = g_breadcrumbs_head; item != NULL; item = item->next) {
            NSError *error = nil;
            NSData *data = [NSData dataWithBytesNoCopy:item->jsonData length:strlen(item->jsonData) freeWhenDone:NO];
            id JSONObject = [BSGJSONSerialization JSONObjectWithData:data options:0 error:&error];
            if (!JSONObject) {
                bsg_log_err(@"Unable to parse breadcrumb: %@", error);
                continue;
            }
            BugsnagBreadcrumb *breadcrumb = nil;
            if (![JSONObject isKindOfClass:[NSDictionary class]] ||
                !(breadcrumb = [BugsnagBreadcrumb breadcrumbFromDict:JSONObject])) {
                bsg_log_err(@"Unexpected breadcrumb payload in buffer");
                continue;
            }
            [breadcrumbs addObject:breadcrumb];
        }
    }
    return breadcrumbs;
}

- (NSArray<BugsnagBreadcrumb *> *)breadcrumbsBeforeDate:(nonnull NSDate *)date {
    // Because breadcrumbs are stored with only millisecond accuracy, we must also round the beforeDate in the same way.
    NSString *dateString = [BSG_RFC3339DateTool stringFromDate:date];
    return BSGArrayMap(self.breadcrumbs, ^id _Nullable(BugsnagBreadcrumb *crumb) {
        // Using `timestampString` is more efficient because `timestamp` is a computed by parsing `timestampString`.
        if ([crumb.timestampString compare:dateString] == NSOrderedDescending) {
            return nil;
        }
        return crumb;
    });
}

- (void)addBreadcrumb:(NSString *)breadcrumbMessage {
    [self addBreadcrumbWithBlock:^(BugsnagBreadcrumb *_Nonnull crumb) {
        crumb.message = breadcrumbMessage;
    }];
}

- (void)addBreadcrumbWithBlock:(BSGBreadcrumbConfiguration)block {
    if (self.maxBreadcrumbs == 0) {
        return;
    }
    BugsnagBreadcrumb *crumb = [BugsnagBreadcrumb breadcrumbWithBlock:block];
    if (!crumb || ![self shouldSendBreadcrumb:crumb]) {
        return;
    }
    NSData *data = [self dataForBreadcrumb:crumb];
    if (!data) {
        return;
    }
    [self addBreadcrumbWithData:data writeToDisk:YES];
}

- (void)addBreadcrumbWithData:(NSData *)data writeToDisk:(BOOL)writeToDisk {
    struct bsg_breadcrumb_list_item *newItem = calloc(1, sizeof(struct bsg_breadcrumb_list_item) + data.length + 1);
    if (!newItem) {
        return;
    }
    [data enumerateByteRangesUsingBlock:^(const void *bytes, NSRange byteRange, __unused BOOL *stop) {
        memcpy(newItem->jsonData + byteRange.location, bytes, byteRange.length);
    }];
    
    @synchronized (self) {
        const unsigned int fileNumber = self.nextFileNumber;
        const BOOL deleteOld = fileNumber >= self.maxBreadcrumbs;
        self.nextFileNumber = fileNumber + 1;
        
        if (g_breadcrumbs_head) {
            struct bsg_breadcrumb_list_item *tail = g_breadcrumbs_head;
            while (tail->next) {
                tail = tail->next;
            }
            tail->next = newItem;
            if (deleteOld) {
                struct bsg_breadcrumb_list_item *head = g_breadcrumbs_head;
                g_breadcrumbs_head = head->next;
                head->next = NULL;
                free(head);
            }
        } else {
            g_breadcrumbs_head = newItem;
        }
        
        if (!writeToDisk) {
            return;
        }
        //
        // Breadcrumbs are also stored on disk so that they are accessible at next
        // launch if an OOM is detected.
        //
        dispatch_async(BSGGlobalsFileSystemQueue(), ^{
            // Avoid writing breadcrumbs that have already been deleted from the in-memory store.
            // This can occur when breadcrumbs are being added faster than they can be written.
            unsigned int nextFileNumber = self.nextFileNumber;
            BOOL isStale = (self.maxBreadcrumbs < nextFileNumber) && (fileNumber < (nextFileNumber - self.maxBreadcrumbs));
            
            NSError *error = nil;
            
            if (!isStale) {
                NSString *file = [self pathForFileNumber:fileNumber];
                // NSDataWritingAtomic not required because we no longer read the files without checking for validity
                if (![data writeToFile:file options:0 error:&error]) {
                    bsg_log_err(@"Unable to write breadcrumb: %@", error);
                }
            }
            
            if (deleteOld) {
                NSString *fileToDelete = [self pathForFileNumber:fileNumber - self.maxBreadcrumbs];
                if (![[[NSFileManager alloc] init] removeItemAtPath:fileToDelete error:&error] &&
                    !([error.domain isEqual:NSCocoaErrorDomain] && error.code == NSFileNoSuchFileError)) {
                    bsg_log_err(@"Unable to delete old breadcrumb: %@", error);
                }
            }
        });
    }
}

- (BOOL)shouldSendBreadcrumb:(BugsnagBreadcrumb *)crumb {
    for (BugsnagOnBreadcrumbBlock block in self.config.onBreadcrumbBlocks) {
        @try {
            if (!block(crumb)) {
                return NO;
            }
        } @catch (NSException *exception) {
            bsg_log_err(@"Error from onBreadcrumb callback: %@", exception);
        }
    }
    return YES;
}

- (void)removeAllBreadcrumbs {
    @synchronized (self) {
        struct bsg_breadcrumb_list_item *item = g_breadcrumbs_head;
        g_breadcrumbs_head = NULL;
        while (item) {
            struct bsg_breadcrumb_list_item *next = item->next;
            free(item);
            item = next;
        }
        self.nextFileNumber = 0;
    }
    [self deleteBreadcrumbFiles];
}

#pragma mark - File storage

- (NSData *)dataForBreadcrumb:(BugsnagBreadcrumb *)breadcrumb {
    id JSONObject = [breadcrumb objectValue];
    if (![BSGJSONSerialization isValidJSONObject:JSONObject]) {
        bsg_log_err(@"Unable to serialize breadcrumb: Not a valid JSON object");
        return nil;
    }
    NSError *error = nil;
    NSData *data = [BSGJSONSerialization dataWithJSONObject:JSONObject options:0 error:&error];
    if (!data) {
        bsg_log_err(@"Unable to serialize breadcrumb: %@", error);
    }
    return data;
}

- (NSString *)pathForFileNumber:(unsigned int)fileNumber {
    return [self.breadcrumbsPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%u.json", fileNumber]];
}

- (NSArray<BugsnagBreadcrumb *> *)cachedBreadcrumbs {
    NSError *error = nil;
    
    NSArray<NSString *> *filenames = [[NSFileManager defaultManager] contentsOfDirectoryAtPath:self.breadcrumbsPath error:&error];
    if (!filenames) {
        bsg_log_err(@"Unable to read breadcrumbs: %@", error);
        return @[];
    }
    
    // We cannot use NSString's -localizedStandardCompare: because its sorting may vary by locale.
    filenames = [filenames sortedArrayUsingComparator:^NSComparisonResult(NSString *name1, NSString *name2) {
        long long value1 = [[name1 stringByDeletingPathExtension] longLongValue];
        long long value2 = [[name2 stringByDeletingPathExtension] longLongValue];
        if (value1 < value2) { return NSOrderedAscending; }
        if (value1 > value2) { return NSOrderedDescending; }
        return NSOrderedSame;
    }];
    
    NSMutableArray *breadcrumbs = [NSMutableArray array];
    
    for (NSString *file in filenames) {
        if ([file hasPrefix:@"."] || ![file.pathExtension isEqual:@"json"]) {
            // Ignore partially written files, which have names like ".dat.nosync43c9.RZFc3z"
            continue;
        }
        NSString *path = [self.breadcrumbsPath stringByAppendingPathComponent:file];
        NSData *data = [NSData dataWithContentsOfFile:path options:0 error:&error];
        if (!data) {
            // If a high volume of breadcrumbs is being logged, it is normal for older files to be deleted before this thread can read them.
            if (!(error.domain == NSCocoaErrorDomain && error.code == NSFileReadNoSuchFileError)) {
                bsg_log_err(@"Unable to read breadcrumb: %@", error);
            }
            continue;
        }
        id JSONObject = [BSGJSONSerialization JSONObjectWithData:data options:0 error:&error];
        if (!JSONObject) {
            bsg_log_err(@"Unable to parse breadcrumb: %@", error);
            continue;
        }
        BugsnagBreadcrumb *breadcrumb;
        if (![JSONObject isKindOfClass:[NSDictionary class]] ||
            !(breadcrumb = [BugsnagBreadcrumb breadcrumbFromDict:JSONObject])) {
            bsg_log_err(@"Unexpected breadcrumb payload in file %@", file);
            continue;
        }
        [breadcrumbs addObject:breadcrumb];
    }
    
    return breadcrumbs;
}

- (void)deleteBreadcrumbFiles {
    [[NSFileManager defaultManager] removeItemAtPath:self.breadcrumbsPath error:NULL];
    
    NSError *error = nil;
    if (![[NSFileManager defaultManager] createDirectoryAtPath:self.breadcrumbsPath withIntermediateDirectories:YES attributes:nil error:&error]) {
        bsg_log_err(@"Unable to create breadcrumbs directory: %@", error);
    }
}

@end

#pragma mark -

void BugsnagBreadcrumbsWriteCrashReport(const BSG_KSCrashReportWriter *writer) {
    writer->beginArray(writer, "breadcrumbs");
    
    for (struct bsg_breadcrumb_list_item *item = g_breadcrumbs_head; item != NULL; item = item->next) {
        writer->addJSONElement(writer, NULL, item->jsonData);
    }
    
    writer->endContainer(writer);
}
