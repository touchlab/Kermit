//
//  BugsnagSessionTrackingPayload.m
//  Bugsnag
//
//  Created by Jamie Lynch on 27/11/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import "BugsnagSessionTrackingPayload.h"

#import "BugsnagApp+Private.h"
#import "BugsnagCollections.h"
#import "BugsnagClient+Private.h"
#import "BugsnagDevice+Private.h"
#import "Bugsnag+Private.h"
#import "BugsnagKeys.h"
#import "BugsnagNotifier.h"
#import "BugsnagSession+Private.h"
#import "BSG_KSSystemInfo.h"
#import "BugsnagConfiguration.h"
#import "BugsnagApp.h"

@interface BugsnagSessionTrackingPayload ()
@property (nonatomic) BugsnagConfiguration *config;
@property(nonatomic, copy) NSString *codeBundleId;
@property (nonatomic) BugsnagNotifier *notifier;
@end

@implementation BugsnagSessionTrackingPayload

- (instancetype)initWithSessions:(NSArray<BugsnagSession *> *)sessions
                          config:(BugsnagConfiguration *)config
                    codeBundleId:(NSString *)codeBundleId
                        notifier:(BugsnagNotifier *)notifier
{
    if ((self = [super init])) {
        _sessions = sessions;
        _config = config;
        _codeBundleId = codeBundleId;
        _notifier = notifier;
    }
    return self;
}

- (NSMutableDictionary *)toJson
{
    NSMutableDictionary *dict = [NSMutableDictionary new];
    NSMutableArray *sessionData = [NSMutableArray new];

    for (BugsnagSession *session in self.sessions) {
        [sessionData addObject:[session toDictionary]];
    }
    dict[@"sessions"] = sessionData;
    dict[BSGKeyNotifier] = [self.notifier toDict];

    // app/device data collection relies on KSCrash reports,
    // need to mimic the JSON structure here
    BugsnagApp *app = self.sessions[0].app;
    dict[BSGKeyApp] = [app toDict];

    BugsnagDevice *device = self.sessions[0].device;
    dict[BSGKeyDevice] = [device toDictionary];
    return dict;
}

@end
