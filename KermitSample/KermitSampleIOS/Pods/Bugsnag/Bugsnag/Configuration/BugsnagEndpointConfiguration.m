//
//  BugsnagEndpointConfiguration.m
//  Bugsnag
//
//  Created by Jamie Lynch on 15/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "BugsnagEndpointConfiguration.h"

@implementation BugsnagEndpointConfiguration

- (instancetype)init {
    if ((self = [super init])) {
        _notify = @"https://notify.bugsnag.com";
        _sessions = @"https://sessions.bugsnag.com";
    }
    return self;
}

- (instancetype)initWithNotify:(NSString *)notify sessions:(NSString *)sessions {
    if ((self = [super init])) {
        _notify = notify;
        _sessions = sessions;
    }
    return self;
}
@end
