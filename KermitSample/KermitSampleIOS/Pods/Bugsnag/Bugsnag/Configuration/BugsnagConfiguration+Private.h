//
//  BugsnagConfiguration+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 26/11/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagConfiguration.h>

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagConfiguration ()

#pragma mark Initializers

- (instancetype)initWithDictionaryRepresentation:(NSDictionary<NSString *, id> *)JSONObject NS_DESIGNATED_INITIALIZER;

#pragma mark Properties

/// The user defaults database to use for persistence of user information.
@property (class, nonatomic) NSUserDefaults *userDefaults;

@property (readonly, nonatomic) NSDictionary<NSString *, id> *dictionaryRepresentation;

@property (copy, nonatomic) BugsnagMetadata *metadata;

@property (readonly, nullable, nonatomic) NSURL *notifyURL;

@property (nonatomic) NSMutableArray<BugsnagOnBreadcrumbBlock> *onBreadcrumbBlocks;

@property (nonatomic) NSMutableArray<BugsnagOnSendErrorBlock> *onSendBlocks;

/// Hooks for modifying sessions before they are sent to Bugsnag. Intended for internal use only by React Native/Unity.
@property (nonatomic) NSMutableArray<BugsnagOnSessionBlock> *onSessionBlocks;

@property (nonatomic) NSMutableSet *plugins;

@property (readonly, nonatomic) BOOL shouldSendReports;

@property (readonly, nonatomic) NSDictionary<NSString *, id> *sessionApiHeaders;

@property (readonly, nullable, nonatomic) NSURL *sessionURL;

@property (readwrite, retain, nonnull, nonatomic) BugsnagUser *user;

#pragma mark Methods

+ (BOOL)isValidApiKey:(NSString *)apiKey;

- (void)deletePersistedUserData;

- (BOOL)shouldDiscardErrorClass:(NSString *)errorClass;

- (BOOL)shouldRecordBreadcrumbType:(BSGBreadcrumbType)breadcrumbType;

/// Throws an NSInvalidArgumentException if the API key is empty or missing.
/// Logs a warning message if the API key is not in the expected format.
- (void)validate;

@end

NS_ASSUME_NONNULL_END
