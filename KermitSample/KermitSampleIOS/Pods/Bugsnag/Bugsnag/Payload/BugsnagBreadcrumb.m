//
//  BugsnagBreadcrumb.m
//
//  Created by Delisa Mason on 9/16/15.
//
//  Copyright (c) 2015 Bugsnag, Inc. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall remain in place
// in this source code.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
#import "BSG_RFC3339DateTool.h"

#import "BugsnagBreadcrumb+Private.h"
#import "BugsnagBreadcrumbs.h"
#import "Bugsnag.h"
#import "BugsnagLogger.h"
#import "BugsnagKeys.h"

typedef void (^BSGBreadcrumbConfiguration)(BugsnagBreadcrumb *_Nonnull);

NSString *BSGBreadcrumbTypeValue(BSGBreadcrumbType type) {
    switch (type) {
    case BSGBreadcrumbTypeLog:
        return @"log";
    case BSGBreadcrumbTypeUser:
        return @"user";
    case BSGBreadcrumbTypeError:
        return BSGKeyError;
    case BSGBreadcrumbTypeState:
        return @"state";
    case BSGBreadcrumbTypeManual:
        return @"manual";
    case BSGBreadcrumbTypeProcess:
        return @"process";
    case BSGBreadcrumbTypeRequest:
        return @"request";
    case BSGBreadcrumbTypeNavigation:
        return @"navigation";
    }
}

BSGBreadcrumbType BSGBreadcrumbTypeFromString(NSString *value) {
    if ([value isEqual:@"log"]) {
        return BSGBreadcrumbTypeLog;
    } else if ([value isEqual:@"user"]) {
        return BSGBreadcrumbTypeUser;
    } else if ([value isEqual:@"error"]) {
        return BSGBreadcrumbTypeError;
    } else if ([value isEqual:@"state"]) {
        return BSGBreadcrumbTypeState;
    } else if ([value isEqual:@"process"]) {
        return BSGBreadcrumbTypeProcess;
    } else if ([value isEqual:@"request"]) {
        return BSGBreadcrumbTypeRequest;
    } else if ([value isEqual:@"navigation"]) {
        return BSGBreadcrumbTypeNavigation;
    } else {
        return BSGBreadcrumbTypeManual;
    }
}


@interface BugsnagBreadcrumb ()

@property (readwrite, nullable, nonatomic) NSDate *timestamp;

@end


@implementation BugsnagBreadcrumb

- (instancetype)init {
    if ((self = [super init])) {
        _timestamp = [NSDate date];
        _type = BSGBreadcrumbTypeManual;
        _metadata = @{};
    }
    return self;
}

- (BOOL)isValid {
    return self.message.length > 0 && (self.timestampString || self.timestamp);
}

- (NSDictionary *)objectValue {
    NSString *timestamp = self.timestampString ?: [BSG_RFC3339DateTool stringFromDate:self.timestamp];
    if (timestamp && self.message.length > 0) {
        NSMutableDictionary *metadata = [NSMutableDictionary new];
        for (NSString *key in self.metadata) {
            metadata[[key copy]] = [self.metadata[key] copy];
        }
        return @{
            // Note: The Bugsnag Error Reporting API specifies that the breadcrumb "message"
            // field should be delivered in as a "name" field.  This comment notes that variance.
            BSGKeyName : [self.message copy],
            BSGKeyTimestamp : timestamp,
            BSGKeyType : BSGBreadcrumbTypeValue(self.type),
            BSGKeyMetadata : metadata
        };
    }
    return nil;
}

// The timestamp is lazily computed from the timestampString to avoid unnecessary
// calls to -dateFromString: (which is expensive) when loading breadcrumbs from disk.

- (NSDate *)timestamp {
    if (!_timestamp) {
        _timestamp = [BSG_RFC3339DateTool dateFromString:self.timestampString];
    }
    return _timestamp;
}

@synthesize timestampString = _timestampString;

- (void)setTimestampString:(NSString *)timestampString {
    _timestampString = [timestampString copy];
    self.timestamp = nil;
}

+ (instancetype)breadcrumbWithBlock:(BSGBreadcrumbConfiguration)block {
    BugsnagBreadcrumb *crumb = [self new];
    if (block) {
        block(crumb);
    }
    if ([crumb isValid]) {
        return crumb;
    }
    return nil;
}

+ (instancetype)breadcrumbFromDict:(NSDictionary *)dict {
    BOOL isValidCrumb = [dict[BSGKeyType] isKindOfClass:[NSString class]]
        && [dict[BSGKeyTimestamp] isKindOfClass:[NSString class]]
        && [BSG_RFC3339DateTool isLikelyDateString:dict[BSGKeyTimestamp]]
        && (
            [dict[BSGKeyMetadata] isKindOfClass:[NSDictionary class]]
            || [dict[@"metadata"] isKindOfClass:[NSDictionary class]] // react-native uses lowercase key
            )
        // Accept legacy 'name' value if provided.
        && ([dict[BSGKeyMessage] isKindOfClass:[NSString class]]
            || [dict[BSGKeyName] isKindOfClass:[NSString class]]);
    if (isValidCrumb) {
        return [self breadcrumbWithBlock:^(BugsnagBreadcrumb *crumb) {
            crumb.message = dict[BSGKeyMessage] ?: dict[BSGKeyName];
            crumb.metadata = dict[BSGKeyMetadata] ?: dict[@"metadata"];
            crumb.timestampString = dict[BSGKeyTimestamp];
            crumb.type = BSGBreadcrumbTypeFromString(dict[BSGKeyType]);
        }];
    }
    return nil;
}

@end

