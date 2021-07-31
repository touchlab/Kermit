//
// Created by Jamie Lynch on 29/11/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import "BugsnagFileStore.h"

#import "BSGFileLocations.h"
#import "BSG_KSCrashReportFields.h"
#import "BSG_KSJSONCodecObjC.h"
#import "BugsnagLogger.h"
#import "NSError+BSG_SimpleConstructor.h"

#pragma mark - Meta Data


/**
 * Metadata class to hold name and creation date for a file, with
 * default comparison based on the creation date (ascending).
 */
@interface BSGFileStoreInfo : NSObject

@property(nonatomic, readonly, retain) NSString *fileId;
@property(nonatomic, readonly, retain) NSDate *creationDate;

+ (BSGFileStoreInfo *)fileStoreInfoWithId:(NSString *)fileId
                          creationDate:(NSDate *)creationDate;

- (instancetype)initWithId:(NSString *)fileId creationDate:(NSDate *)creationDate;

- (NSComparisonResult)compare:(BSGFileStoreInfo *)other;

@end

@implementation BSGFileStoreInfo

@synthesize fileId = _fileId;
@synthesize creationDate = _creationDate;

+ (BSGFileStoreInfo *)fileStoreInfoWithId:(NSString *)fileId
                          creationDate:(NSDate *)creationDate {
    return [[self alloc] initWithId:fileId creationDate:creationDate];
}

- (instancetype)initWithId:(NSString *)fileId creationDate:(NSDate *)creationDate {
    if ((self = [super init])) {
        _fileId = fileId;
        _creationDate = creationDate;
    }
    return self;
}

- (NSComparisonResult)compare:(BSGFileStoreInfo *)other {
    return [self.creationDate compare:other.creationDate];
}

@end

#pragma mark - Main Class


@interface BugsnagFileStore ()

@property(nonatomic, readwrite, retain) NSString *path;
@property(nonatomic, readonly, retain) NSString *filenameSuffix;

@end


@implementation BugsnagFileStore

#pragma mark Properties

@synthesize path = _path;

#pragma mark Construction

- (instancetype)initWithPath:(NSString *)path
              filenameSuffix:(NSString *)filenameSuffix {
    if ((self = [super init])) {
        self.path = path;
        _filenameSuffix = filenameSuffix;
        self.bundleName = [NSBundle.mainBundle infoDictionary][@"CFBundleName"];
    }
    return self;
}

#pragma mark API

- (NSArray *)fileIds {
    NSError *error = nil;
    NSFileManager *fm = [NSFileManager defaultManager];
    NSArray *filenames = [fm contentsOfDirectoryAtPath:self.path error:&error];
    if (filenames == nil) {
        bsg_log_err(@"Could not get contents of directory %@: %@",
                self.path, error);
        return nil;
    }

    NSMutableArray *files = [NSMutableArray arrayWithCapacity:[filenames count]];

    for (NSString *filename in filenames) {
        NSString *fileId = [self fileIdFromFilename:filename];
        if (fileId != nil) {
            NSString *fullPath =
                    [self.path stringByAppendingPathComponent:filename];
            NSDictionary *fileAttribs =
                    [fm attributesOfItemAtPath:fullPath error:&error];
            if (fileAttribs == nil) {
                bsg_log_err(@"Could not read file attributes for %@: %@",
                        fullPath, error);
            } else {
                BSGFileStoreInfo *info = [BSGFileStoreInfo fileStoreInfoWithId:fileId
                                                            creationDate:[fileAttribs valueForKey:NSFileCreationDate]];
                [files addObject:info];
            }
        }
    }
    [files sortUsingSelector:@selector(compare:)];

    NSMutableArray *sortedIDs =
            [NSMutableArray arrayWithCapacity:[files count]];
    for (BSGFileStoreInfo *info in files) {
        [sortedIDs addObject:info.fileId];
    }
    return sortedIDs;
}

- (NSUInteger)fileCount {
    return [self.fileIds count];
}

- (NSDictionary <NSString *, NSDictionary *> *)allFilesByName {
    NSArray *fileIds = [self fileIds];
    NSMutableDictionary *files =
    [NSMutableDictionary dictionaryWithCapacity:[fileIds count]];
    for (NSString *fileId in fileIds) {
        files[fileId] = [self fileWithId:fileId];
    }
    return files;
}

- (void)pruneFilesLeaving:(int)numFiles {
    NSArray *fileIds = [self fileIds];
    int deleteCount = (int) [fileIds count] - numFiles;
    for (int i = 0; i < deleteCount; i++) {
        [self deleteFileWithId:fileIds[(NSUInteger) i]];
    }
}

- (NSDictionary *)fileWithId:(NSString *)fileId {
    NSError *error = nil;
    NSMutableDictionary *fileContents =
            [self readFile:[self pathToFileWithId:fileId] error:&error];
    if (error != nil) {
        bsg_log_err(@"Encountered error loading file %@: %@",
                fileId, error);
    }
    if (fileContents == nil) {
        bsg_log_err(@"Could not load file");
        return nil;
    }
    return fileContents;
}

- (void)deleteFileWithId:(NSString *)fileId {
    NSError *error = nil;
    NSString *filename = [self pathToFileWithId:fileId];

    [[NSFileManager defaultManager] removeItemAtPath:filename error:&error];
    if (error != nil) {
        bsg_log_err(@"Could not delete file %@: %@", filename, error);
    }
}

#pragma mark Utility

- (void)performOnFields:(NSArray *)fieldPath
                 inFile:(NSMutableDictionary *)file
              operation:(nonnull void (^)(id parent, id field))operation
           okIfNotFound:(BOOL)isOkIfNotFound {
    if (fieldPath.count == 0) {
        bsg_log_err(@"Unexpected end of field path");
        return;
    }

    NSString *currentField = fieldPath[0];
    if (fieldPath.count > 1) {
        fieldPath =
                [fieldPath subarrayWithRange:NSMakeRange(1, fieldPath.count - 1)];
    } else {
        fieldPath = @[];
    }

    id field = file[currentField];
    if (field == nil) {
        if (!isOkIfNotFound) {
            bsg_log_err(@"%@: No such field in file. Candidates are: %@",
                    currentField, file.allKeys);
        }
        return;
    }

    if ([field isKindOfClass:NSMutableDictionary.class]) {
        [self performOnFields:fieldPath
                       inFile:field
                    operation:operation
                 okIfNotFound:isOkIfNotFound];
    } else if ([field isKindOfClass:[NSMutableArray class]]) {
        for (id subfield in field) {
            if ([subfield isKindOfClass:NSMutableDictionary.class]) {
                [self performOnFields:fieldPath
                               inFile:subfield
                            operation:operation
                         okIfNotFound:isOkIfNotFound];
            } else {
                operation(field, subfield);
            }
        }
    } else {
        operation(file, field);
    }
}

- (NSString *)pathToFileWithId:(NSString *)fileId {
    NSString *filename = [self filenameWithId:fileId];
    return [self.path stringByAppendingPathComponent:filename];
}

- (NSMutableDictionary *)readFile:(NSString *)path
                            error:(NSError *__autoreleasing *)error {
    if (path == nil) {
        [NSErrorBSG bsg_fillError:error
                    withDomain:[[self class] description]
                          code:0
                   description:@"Path is nil"];
        return nil;
    }

    NSData *jsonData =
            [NSData dataWithContentsOfFile:path options:0 error:error];
    if (jsonData == nil) {
        return nil;
    }

    NSMutableDictionary *fileContents =
            [BSG_KSJSONCodec decode:jsonData
                              error:error];
    if (error != nil && *error != nil) {

        bsg_log_err(@"Error decoding JSON data from %@: %@", path, *error);
        fileContents[@BSG_KSCrashField_Incomplete] = @YES;
    }
    return fileContents;
}


- (NSString *)filenameWithId:(NSString *)fileId {
    // e.g. Bugsnag Test App-CrashReport-54D4FF86-C3D1-4167-8485-3D7539FDFFF5.json
    return [NSString stringWithFormat:@"%@%@%@.json", self.bundleName, self.filenameSuffix, fileId];
}

- (NSString *)fileIdFromFilename:(NSString *)filename {
    if ([filename length] == 0 ||
            ![[filename pathExtension] isEqualToString:@"json"]) {
        return nil;
    }

    NSString *prefix = [NSString stringWithFormat:@"%@%@", self.bundleName, self.filenameSuffix];
    NSString *suffix = @".json";

    NSRange prefixRange = [filename rangeOfString:prefix];
    NSRange suffixRange =
            [filename rangeOfString:suffix options:NSBackwardsSearch];
    if (prefixRange.location == 0 && suffixRange.location != NSNotFound) {
        NSUInteger prefixEnd = NSMaxRange(prefixRange);
        NSRange range =
                NSMakeRange(prefixEnd, suffixRange.location - prefixEnd);
        return [filename substringWithRange:range];
    }
    return nil;
}


@end
