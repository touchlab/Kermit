//
//  BugsnagBreadcrumb+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagBreadcrumb.h"

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagBreadcrumb ()

+ (nullable instancetype)breadcrumbFromDict:(NSDictionary *)dict;

+ (nullable instancetype)breadcrumbWithBlock:(void (^)(BugsnagBreadcrumb *))block;

- (nullable NSDictionary *)objectValue;

/// String representation of `timestamp` used to avoid unnecessary date <--> string conversions
@property (copy, nullable, nonatomic) NSString *timestampString;

@end

FOUNDATION_EXPORT NSString *BSGBreadcrumbTypeValue(BSGBreadcrumbType type);
FOUNDATION_EXPORT BSGBreadcrumbType BSGBreadcrumbTypeFromString( NSString * _Nullable value);

NS_ASSUME_NONNULL_END
