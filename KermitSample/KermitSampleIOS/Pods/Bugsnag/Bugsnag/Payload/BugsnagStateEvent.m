//
//  BugsnagStateEvent.m
//  Bugsnag
//
//  Created by Jamie Lynch on 18/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "BugsnagStateEvent.h"

@implementation BugsnagStateEvent

- (instancetype)initWithName:(NSString *)name
                        data:(id)data
{
    if ((self = [super init])) {
        self.type = name;
        self.data = data;
    }
    return self;
}

@end
