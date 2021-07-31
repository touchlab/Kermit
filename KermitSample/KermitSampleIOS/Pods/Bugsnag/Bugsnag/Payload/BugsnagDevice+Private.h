//
//  BugsnagDevice+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagDevice.h"

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagDevice ()

+ (instancetype)deviceWithKSCrashReport:(NSDictionary *)event;

+ (instancetype)deserializeFromJson:(NSDictionary *)json;

+ (void)populateFields:(BugsnagDevice *)device dictionary:(NSDictionary *)event;

- (void)appendRuntimeInfo:(NSDictionary *)info;

- (NSDictionary *)toDictionary;

@end

NS_ASSUME_NONNULL_END
