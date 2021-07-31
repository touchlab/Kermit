//
//  BugsnagSession.h
//  Bugsnag
//
//  Created by Jamie Lynch on 24/11/2017.
//  Copyright © 2017 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

#import <Bugsnag/BugsnagApp.h>
#import <Bugsnag/BugsnagDevice.h>
#import <Bugsnag/BugsnagUser.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Represents a session of user interaction with your app.
 */
@interface BugsnagSession : NSObject

@property (copy, nonatomic) NSString *id;

@property (strong, nonatomic) NSDate *startedAt;

@property (readonly, nonatomic) BugsnagApp *app;

@property (readonly, nonatomic) BugsnagDevice *device;

// =============================================================================
// MARK: - User
// =============================================================================

/**
 * The current user
 */
@property (readonly, nonnull, nonatomic) BugsnagUser *user;

/**
 *  Set user metadata
 *
 *  @param userId ID of the user
 *  @param name   Name of the user
 *  @param email  Email address of the user
 */
- (void)setUser:(nullable NSString *)userId withEmail:(nullable NSString *)email andName:(nullable NSString *)name;

@end

NS_ASSUME_NONNULL_END
