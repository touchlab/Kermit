//
// Created by Jamie Lynch on 29/11/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface BugsnagFileStore : NSObject

/** Location where files are stored. */
@property(nonatomic, readonly, retain) NSString *path;

/** The total number of files. Note: This is an expensive operation. */
@property(nonatomic, readonly, assign) NSUInteger fileCount;
@property(nonatomic, readwrite, retain) NSString *bundleName;

/** Initialize a store.
 *
 * @param path Where to store files.
 *
 * @return The initialized file store.
 */
- (instancetype)initWithPath:(NSString *)path
              filenameSuffix:(NSString *)filenameSuffix;

/** Fetch a file.
 *
 * @param fileId The ID of the file to fetch.
 *
 * @return The file or nil if not found.
 */
- (NSDictionary *)fileWithId:(NSString *)fileId;

/**
 * @return A list of all files IDs in chronological order (oldest first).
 */
- (NSArray *)fileIds;

/** Get a list of all files by filename.
 *
 * @return A collection of file contents indexed by filename.
 */
- (NSDictionary <NSString *, NSDictionary *> *)allFilesByName;

/** Delete a file.
 *
 * @param fileId The file ID.
 */
- (void)deleteFileWithId:(NSString *)fileId;

/** Prune files, keeping only the newest ones.
 *
 * @param numFiles the number of files to keep.
 */
- (void)pruneFilesLeaving:(int)numFiles;

/** Full path to the file with the specified ID.
 *
 * @param fileId The file ID
 *
 * @return The full path.
 */
- (NSString *)pathToFileWithId:(NSString *)fileId;

- (NSMutableDictionary *)readFile:(NSString *)path
                            error:(NSError *__autoreleasing *)error;

@end
