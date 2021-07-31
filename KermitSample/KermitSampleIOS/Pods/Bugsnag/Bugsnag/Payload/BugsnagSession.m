//
//  BugsnagSession.m
//  Bugsnag
//
//  Created by Jamie Lynch on 24/11/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import "BugsnagSession+Private.h"

#import "BugsnagApp+Private.h"
#import "BugsnagCollections.h"
#import "BugsnagDevice+Private.h"
#import "BugsnagUser+Private.h"
#import "BSG_RFC3339DateTool.h"
#import "BugsnagKeys.h"

static NSString *const kBugsnagSessionId = @"id";
static NSString *const kBugsnagUnhandledCount = @"unhandledCount";
static NSString *const kBugsnagHandledCount = @"handledCount";
static NSString *const kBugsnagStartedAt = @"startedAt";
static NSString *const kBugsnagUser = @"user";

@implementation BugsnagSession

- (instancetype)initWithId:(NSString *)sessionId
                 startDate:(NSDate *)startDate
                      user:(BugsnagUser *)user
              autoCaptured:(BOOL)autoCaptured
                       app:(BugsnagApp *)app
                    device:(BugsnagDevice *)device {
    if ((self = [super init])) {
        _id = sessionId;
        _startedAt = [startDate copy];
        _user = user;
        _autoCaptured = autoCaptured;
        _app = app;
        _device = device;
    }
    return self;
}

+ (nullable instancetype)fromJson:(NSDictionary *)json {
    if (!json) {
        return nil;
    }
    NSString *sessionId = BSGDeserializeString(json[kBugsnagSessionId]);
    if (!sessionId) {
        return nil;
    }
    NSDictionary *events = json[@"events"];
    return [[BugsnagSession alloc] initWithId:sessionId
                                    startDate:BSGDeserializeDate(json[kBugsnagStartedAt]) ?: [NSDate date]
                                         user:[[BugsnagUser alloc] init]
                                 handledCount:[events[@"handled"] unsignedIntegerValue]
                               unhandledCount:[events[@"unhandled"] unsignedIntegerValue]
                                          app:[[BugsnagApp alloc] init]
                                       device:[[BugsnagDevice alloc] init]];
}

- (nullable instancetype)initWithDictionary:(NSDictionary *)dict {
    NSString *sessionId = BSGDeserializeString(dict[kBugsnagSessionId]);
    if (!sessionId) {
        return nil;
    }
    if ((self = [super init])) {
        _id = sessionId;
        _unhandledCount = [dict[kBugsnagUnhandledCount] unsignedIntegerValue];
        _handledCount = [dict[kBugsnagHandledCount] unsignedIntegerValue];

        _startedAt = BSGDeserializeDate(dict[kBugsnagStartedAt]) ?: [NSDate date];

        _user = BSGDeserializeObject(dict[kBugsnagUser], ^id _Nullable(NSDictionary * _Nonnull json) {
            return [[BugsnagUser alloc] initWithDictionary:json];
        }) ?: [[BugsnagUser alloc] initWithDictionary:@{}];

        _app = BSGDeserializeObject(dict[BSGKeyApp], ^id _Nullable(NSDictionary * _Nonnull json) {
            return [BugsnagApp deserializeFromJson:json];
        }) ?: [[BugsnagApp alloc] init];

        _device = BSGDeserializeObject(dict[BSGKeyDevice], ^id _Nullable(NSDictionary * _Nonnull json) {
            return [BugsnagDevice deserializeFromJson:json];
        }) ?: [[BugsnagDevice alloc] init];
    }
    return self;
}

- (_Nonnull instancetype)initWithId:(NSString *)sessionId
                          startDate:(NSDate *)startDate
                               user:(BugsnagUser *)user
                       handledCount:(NSUInteger)handledCount
                     unhandledCount:(NSUInteger)unhandledCount
                                app:(BugsnagApp *)app
                             device:(BugsnagDevice *)device {
    if ((self = [super init])) {
        _id = sessionId;
        _startedAt = startDate;
        _unhandledCount = unhandledCount;
        _handledCount = handledCount;
        _user = user;
        _app = app;
        _device = device;
    }
    return self;
}

- (nonnull id)copyWithZone:(nullable __attribute__((unused)) NSZone *)zone {
    return [[BugsnagSession alloc] initWithId:self.id
                                    startDate:self.startedAt
                                         user:self.user
                                 handledCount:self.handledCount
                               unhandledCount:self.unhandledCount
                                          app:self.app
                                       device:self.device];
}

- (NSDictionary *)toJson {
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[kBugsnagSessionId] = self.id;
    dict[kBugsnagStartedAt] = [BSG_RFC3339DateTool stringFromDate:self.startedAt];

    if (self.user) {
        dict[kBugsnagUser] = [self.user toJson];
    }

    dict[BSGKeyApp] = [self.app toDict];
    dict[BSGKeyDevice] = [self.device toDictionary];
    return [NSDictionary dictionaryWithDictionary:dict];
}

- (NSDictionary *)toDictionary {
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[kBugsnagSessionId] = self.id ?: @"";
    dict[kBugsnagStartedAt] = self.startedAt ? [BSG_RFC3339DateTool stringFromDate:self.startedAt] : @"";
    dict[kBugsnagHandledCount] = @(self.handledCount);
    dict[kBugsnagUnhandledCount] = @(self.unhandledCount);
    if (self.user) {
        dict[kBugsnagUser] = [self.user toJson];
    }
    return dict;
}

- (void)stop {
    self.stopped = YES;
}

- (void)resume {
    self.stopped = NO;
}

- (void)setUser:(NSString *_Nullable)userId
      withEmail:(NSString *_Nullable)email
        andName:(NSString *_Nullable)name {
    self.user = [[BugsnagUser alloc] initWithUserId:userId name:name emailAddress:email];
}

@end
