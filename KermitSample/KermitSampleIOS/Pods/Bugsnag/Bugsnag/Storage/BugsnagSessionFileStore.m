//
// Created by Jamie Lynch on 30/11/2017.
// Copyright (c) 2017 Bugsnag. All rights reserved.
//

#import "BugsnagSessionFileStore.h"

#import "BSGJSONSerialization.h"
#import "BugsnagLogger.h"
#import "BugsnagSession+Private.h"

static NSString *const kSessionStoreSuffix = @"-Session-";

@interface BugsnagSessionFileStore ()

@property (nonatomic) NSUInteger maxPersistedSessions;

@end

@implementation BugsnagSessionFileStore

+ (BugsnagSessionFileStore *)storeWithPath:(NSString *)path
                      maxPersistedSessions:(NSUInteger)maxPersistedSessions {
    return [[self alloc] initWithPath:path
                 maxPersistedSessions:maxPersistedSessions];
}

- (instancetype) initWithPath:(NSString *)path
         maxPersistedSessions:(NSUInteger)maxPersistedSessions {
    if ((self = [super initWithPath:path
                     filenameSuffix:kSessionStoreSuffix])) {
        _maxPersistedSessions = maxPersistedSessions;
    }
    return self;
}

- (void)write:(BugsnagSession *)session {
    // serialise session
    NSString *filepath = [self pathToFileWithId:session.id];
    NSDictionary *dict = [session toJson];

    NSError *error;
    NSData *json = [BSGJSONSerialization dataWithJSONObject:dict options:0 error:&error];

    if (error != nil || ![json writeToFile:filepath atomically:YES]) {
        bsg_log_err(@"Failed to write session %@", error);
        return;
    }
    
    [self pruneFilesLeaving:(int)self.maxPersistedSessions];
}


@end
