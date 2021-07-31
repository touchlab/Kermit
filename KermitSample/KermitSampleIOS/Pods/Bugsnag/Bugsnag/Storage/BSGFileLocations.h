//
//  BSGFileLocations.h
//  Bugsnag
//
//  Created by Karl Stenerud on 05.01.21.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BSGFileLocations : NSObject

@property (readonly, nonatomic) NSString *kvStore;
@property (readonly, nonatomic) NSString *breadcrumbs;
@property (readonly, nonatomic) NSString *events;
@property (readonly, nonatomic) NSString *kscrashReports;
@property (readonly, nonatomic) NSString *sessions;

/**
 * File containing details of the current app hang (if the app is hung)
 */
@property (readonly, nonatomic) NSString *appHangEvent;

/**
 * File whose presence indicates that the libary at least attempted to handle the last
 * crash (in case it crashed before writing enough information).
 */
@property (readonly, nonatomic) NSString *flagHandledCrash;

/**
 * Bugsnag client configuration
 */
@property (readonly, nonatomic) NSString *configuration;

/**
 * General per-launch metadata
 */
@property (readonly, nonatomic) NSString *metadata;

/**
 * State info that gets added to the low level crash report.
 */
@property (readonly, nonatomic) NSString *state;

/**
 * State information about the app and operating envronment.
 */
@property (readonly, nonatomic) NSString *systemState;

+ (instancetype) current;
+ (instancetype) v1;

@end

NS_ASSUME_NONNULL_END
