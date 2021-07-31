//
//  BugsnagBreadcrumbs.h
//  Bugsnag
//
//  Created by Jamie Lynch on 26/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BugsnagBreadcrumb;
@class BugsnagConfiguration;
typedef struct BSG_KSCrashReportWriter BSG_KSCrashReportWriter;

typedef void (^BSGBreadcrumbConfiguration)(BugsnagBreadcrumb *_Nonnull);

#pragma mark -

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagBreadcrumbs : NSObject

- (instancetype)initWithConfiguration:(BugsnagConfiguration *)config;

/**
 * The breadcrumbs stored in memory.
 */
@property (readonly, nonatomic) NSArray<BugsnagBreadcrumb *> *breadcrumbs;

/**
 * Store a new breadcrumb with a provided message.
 */
- (void)addBreadcrumb:(NSString *)breadcrumbMessage;

/**
 *  Store a new breadcrumb configured via block.
 *
 *  @param block configuration block
 */
- (void)addBreadcrumbWithBlock:(BSGBreadcrumbConfiguration)block;

/**
 * Store a new serialized breadcrumb.
 *
 * This method is not intended to be used from other classes, it is exposed to facilitate unit testing.
 */
- (void)addBreadcrumbWithData:(NSData *)data writeToDisk:(BOOL)writeToDisk;

- (NSArray<BugsnagBreadcrumb *> *)breadcrumbsBeforeDate:(NSDate *)date;

/**
 * The breadcrumb stored on disk.
 */
- (NSArray<BugsnagBreadcrumb *> *)cachedBreadcrumbs;

/**
 * Removes breadcrumbs from disk.
 */
- (void)removeAllBreadcrumbs;

@end

NS_ASSUME_NONNULL_END

#pragma mark -

/**
 * Inserts the current breadcrumbs into a crash report.
 *
 * This function is async-signal-safe, but requires that any threads that could be adding
 * breadcrumbs are suspended.
 */
void BugsnagBreadcrumbsWriteCrashReport(const BSG_KSCrashReportWriter * _Nonnull writer);
