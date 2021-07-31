//
//  BSGBreadcrumbsProducer.h
//  Bugsnag
//
//  Created by Nick Dowell on 10/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagBreadcrumb.h>

@class BugsnagConfiguration;

NS_ASSUME_NONNULL_BEGIN

@protocol BSGBreadcrumbSink <NSObject>

- (void)leaveBreadcrumbWithMessage:(NSString *)message metadata:(nullable NSDictionary *)metadata andType:(BSGBreadcrumbType)type;

@end


#pragma mark -

extern NSString * const BSGNotificationBreadcrumbsMessageAppWillTerminate;

@interface BSGNotificationBreadcrumbs : NSObject

#pragma mark Initializers

- (instancetype)initWithConfiguration:(BugsnagConfiguration *)configuration
                       breadcrumbSink:(id<BSGBreadcrumbSink>)breadcrumbSink NS_DESIGNATED_INITIALIZER;

- (instancetype)init UNAVAILABLE_ATTRIBUTE;

#pragma mark Properties

@property (nonatomic) BugsnagConfiguration *configuration;

@property (weak, nonatomic) id<BSGBreadcrumbSink> breadcrumbSink;

@property (nonatomic) NSNotificationCenter *notificationCenter;

@property (nonatomic) NSNotificationCenter *workspaceNotificationCenter;

#pragma mark Methods

/// Starts observing the default notifications.
- (void)start;

/// Starts observing notifications with the given name and adds a "state" breadcrumbs when received.
- (void)startListeningForStateChangeNotification:(NSNotificationName)notificationName;

- (NSString *)messageForNotificationName:(NSNotificationName)name;

@end

NS_ASSUME_NONNULL_END
