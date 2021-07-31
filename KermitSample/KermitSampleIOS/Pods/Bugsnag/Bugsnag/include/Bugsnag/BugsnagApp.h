//
//  BugsnagApp.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * Stateless information set by the notifier about your app can be found on this class. These values
 * can be accessed and amended if necessary.
 */
@interface BugsnagApp : NSObject

/**
 * The architecture of the running binary
 */
@property (copy, nullable, nonatomic) NSString *binaryArch;

/**
 * The bundle version used by the application
 */
@property (copy, nullable, nonatomic) NSString *bundleVersion;

/**
 * The revision ID from the manifest (React Native apps only)
 */
@property (copy, nullable, nonatomic) NSString *codeBundleId;

/**
 * Unique identifier for the debug symbols file corresponding to the application
 */
@property (copy, nullable, nonatomic) NSString *dsymUuid;

/**
 * The app identifier used by the application
 */
@property (copy, nullable, nonatomic) NSString *id;

/**
 * The release stage set in Configuration
 */
@property (copy, nullable, nonatomic) NSString *releaseStage;

/**
 * The application type set in Configuration
 */
@property (copy, nullable, nonatomic) NSString *type;

/**
 * The version of the application set in Configuration
 */
@property (copy, nullable, nonatomic) NSString *version;

@end
