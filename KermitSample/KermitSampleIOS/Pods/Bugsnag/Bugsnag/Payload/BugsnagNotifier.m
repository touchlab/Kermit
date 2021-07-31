//
//  BugsnagNotifier.m
//  Bugsnag
//
//  Created by Jamie Lynch on 29/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "BugsnagPlatformConditional.h"

#import "BugsnagNotifier.h"

@implementation BugsnagNotifier

- (instancetype)init {
    if ((self = [super init])) {
#if BSG_PLATFORM_TVOS
        self.name = @"tvOS Bugsnag Notifier";
#elif BSG_PLATFORM_IOS
        self.name = @"iOS Bugsnag Notifier";
#elif BSG_PLATFORM_OSX
        self.name = @"OSX Bugsnag Notifier";
#else
        self.name = @"Bugsnag Objective-C";
#endif
        self.version = @"6.10.2";
        self.url = @"https://github.com/bugsnag/bugsnag-cocoa";
        self.dependencies = [NSMutableArray new];
    }
    return self;
}

- (NSDictionary *)toDict {
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[@"name"] = self.name;
    dict[@"version"] = self.version;
    dict[@"url"] = self.url;

    if ([self.dependencies count] > 0) {
        NSMutableArray *values = [NSMutableArray new];
        dict[@"dependencies"] = values;

        for (BugsnagNotifier *notifier in self.dependencies) {
            [values addObject:[notifier toDict]];
        }
    }
    return dict;
}

@end
