//
//  BugsnagUser.m
//  Bugsnag
//
//  Created by Jamie Lynch on 24/11/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import "BugsnagUser+Private.h"

#import "BugsnagCollections.h"

@implementation BugsnagUser

- (instancetype)initWithDictionary:(NSDictionary *)dict {
    if ((self = [super init])) {
        _id = dict[@"id"];
        _email = dict[@"email"];
        _name = dict[@"name"];
    }
    return self;
}

- (instancetype)initWithUserId:(NSString *)userId name:(NSString *)name emailAddress:(NSString *)emailAddress {
    self = [super init];
    if (self) {
        _id = userId;
        _name = name;
        _email = emailAddress;
    }
    return self;
}

+ (instancetype)userWithUserId:(NSString *)userId name:(NSString *)name emailAddress:(NSString *)emailAddress {
    return [[self alloc] initWithUserId:userId name:name emailAddress:emailAddress];
}

- (NSDictionary *)toJson {
    NSMutableDictionary *dict = [NSMutableDictionary new];
    dict[@"id"] = self.id;
    dict[@"email"] = self.email;
    dict[@"name"] = self.name;
    return [NSDictionary dictionaryWithDictionary:dict];
}

@end
