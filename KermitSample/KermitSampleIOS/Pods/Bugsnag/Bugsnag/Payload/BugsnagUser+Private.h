//
//  BugsnagUser+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagUser.h"

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagUser ()

- (instancetype)initWithDictionary:(NSDictionary *)dict;

- (instancetype)initWithUserId:(nullable NSString *)userId name:(nullable NSString *)name emailAddress:(nullable NSString *)emailAddress;

- (NSDictionary *)toJson;

@end

NS_ASSUME_NONNULL_END
