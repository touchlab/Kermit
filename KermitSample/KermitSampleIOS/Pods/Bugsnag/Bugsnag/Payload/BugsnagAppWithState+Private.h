//
//  BugsnagAppWithState+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagAppWithState.h"
#import "BugsnagApp+Private.h"

@class BugsnagConfiguration;

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagAppWithState ()

+ (BugsnagAppWithState *)appFromJson:(NSDictionary *)json;

+ (BugsnagAppWithState *)appWithDictionary:(NSDictionary *)event config:(BugsnagConfiguration *)config codeBundleId:(nullable NSString *)codeBundleId;

- (NSDictionary *)toDict;

@end

NS_ASSUME_NONNULL_END
