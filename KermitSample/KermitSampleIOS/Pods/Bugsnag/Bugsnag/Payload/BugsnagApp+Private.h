//
//  BugsnagApp+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagApp.h"

@class BugsnagConfiguration;

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagApp ()

+ (BugsnagApp *)appWithDictionary:(NSDictionary *)event config:(BugsnagConfiguration *)config codeBundleId:(NSString *)codeBundleId;

+ (BugsnagApp *)deserializeFromJson:(NSDictionary *)json;

+ (void)populateFields:(BugsnagApp *)app dictionary:(NSDictionary *)event config:(BugsnagConfiguration *)config codeBundleId:(NSString *)codeBundleId;

- (void)setValuesFromConfiguration:(BugsnagConfiguration *)configuration;

- (NSDictionary *)toDict;

@end

NSDictionary *BSGParseAppMetadata(NSDictionary *event);

NS_ASSUME_NONNULL_END
