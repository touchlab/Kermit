//
//  BugsnagAppWithState.m
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "BugsnagAppWithState+Private.h"

#import "BSG_KSCrashReportFields.h"
#import "BugsnagApp+Private.h"
#import "BugsnagKeys.h"

@implementation BugsnagAppWithState

+ (BugsnagAppWithState *)appFromJson:(NSDictionary *)json {
    BugsnagAppWithState *app = [BugsnagAppWithState new];

    id duration = json[@"duration"];
    if ([duration isKindOfClass:[NSNumber class]]) {
        app.duration = duration;
    }

    id durationInForeground = json[@"durationInForeground"];
    if ([durationInForeground isKindOfClass:[NSNumber class]]) {
        app.durationInForeground = durationInForeground;
    }

    id inForeground = json[@"inForeground"];
    if (inForeground) {
        app.inForeground = [(NSNumber *) inForeground boolValue];
    }

    NSArray *dsyms = json[@"dsymUUIDs"];

    if (dsyms.count) {
        app.dsymUuid = dsyms[0];
    }

    app.binaryArch = json[@"binaryArch"];
    app.bundleVersion = json[@"bundleVersion"];
    app.codeBundleId = json[@"codeBundleId"];
    app.id = json[@"id"];
    app.releaseStage = json[@"releaseStage"];
    app.type = json[@"type"];
    app.version = json[@"version"];
    app.isLaunching = [json[@"isLaunching"] boolValue];
    return app;
}

+ (BugsnagAppWithState *)appWithDictionary:(NSDictionary *)event
                                    config:(BugsnagConfiguration *)config
                              codeBundleId:(NSString *)codeBundleId
{
    BugsnagAppWithState *app = [BugsnagAppWithState new];
    NSDictionary *system = event[BSGKeySystem];
    NSDictionary *stats = system[@BSG_KSCrashField_AppStats];

    // convert from seconds to milliseconds
    NSNumber *activeTimeSinceLaunch = @((int)([stats[@BSG_KSCrashField_ActiveTimeSinceLaunch] doubleValue] * 1000.0));
    NSNumber *backgroundTimeSinceLaunch = @((int)([stats[@BSG_KSCrashField_BGTimeSinceLaunch] doubleValue] * 1000.0));

    app.durationInForeground = activeTimeSinceLaunch;
    app.duration = @([activeTimeSinceLaunch longValue] + [backgroundTimeSinceLaunch longValue]);
    app.inForeground = [stats[@BSG_KSCrashField_AppInFG] boolValue];
    app.isLaunching = [[event valueForKeyPath:@"user.state.app.isLaunching"] boolValue];
    [BugsnagApp populateFields:app dictionary:event config:config codeBundleId:codeBundleId];
    return app;
}

- (NSDictionary *)toDict
{
    NSMutableDictionary *dict = [[super toDict] mutableCopy];
    dict[@"duration"] = self.duration;
    dict[@"durationInForeground"] = self.durationInForeground;
    dict[@"inForeground"] = @(self.inForeground);
    dict[@"isLaunching"] = @(self.isLaunching);
    return dict;
}

@end
