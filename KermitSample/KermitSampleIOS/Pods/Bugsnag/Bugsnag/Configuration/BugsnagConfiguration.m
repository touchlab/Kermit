//
//  BugsnagConfiguration.m
//
//  Created by Conrad Irwin on 2014-10-01.
//
//  Copyright (c) 2014 Bugsnag, Inc. All rights reserved.
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

#import "BugsnagPlatformConditional.h"

#import "BugsnagConfiguration+Private.h"

#import "BSGConfigurationBuilder.h"
#import "BSG_RFC3339DateTool.h"
#import "BugsnagApiClient.h"
#import "BugsnagEndpointConfiguration.h"
#import "BugsnagErrorTypes.h"
#import "BugsnagKeys.h"
#import "BugsnagLogger.h"
#import "BugsnagMetadata+Private.h"
#import "BugsnagUser+Private.h"

const NSUInteger BugsnagAppHangThresholdFatalOnly = INT_MAX;

static const int BSGApiKeyLength = 32;

// User info persistence keys
static NSString * const kBugsnagUserEmailAddress = @"BugsnagUserEmailAddress";
static NSString * const kBugsnagUserName = @"BugsnagUserName";
static NSString * const kBugsnagUserUserId = @"BugsnagUserUserId";

// =============================================================================
// MARK: - BugsnagConfiguration
// =============================================================================

@implementation BugsnagConfiguration

static NSUserDefaults *userDefaults;

+ (void)initialize {
    userDefaults = NSUserDefaults.standardUserDefaults;
}

+ (instancetype _Nonnull)loadConfig {
    NSDictionary *options = [[NSBundle mainBundle] infoDictionary][@"bugsnag"];
    return [BSGConfigurationBuilder configurationFromOptions:options];
}

+ (instancetype)loadConfigFromOptions:(NSDictionary *)options {
    return [BSGConfigurationBuilder configurationFromOptions:options];
}

// -----------------------------------------------------------------------------
// MARK: - <NSCopying>
// -----------------------------------------------------------------------------

/**
 * Produce a shallow copy of the BugsnagConfiguration object.
 *
 * @param zone This parameter is ignored. Memory zones are no longer used by Objective-C.
 */
- (nonnull id)copyWithZone:(nullable __attribute__((unused)) NSZone *)zone {
    BugsnagConfiguration *copy = [[BugsnagConfiguration alloc] initWithApiKey:[self.apiKey copy]];
    // Omit apiKey - it's set explicitly in the line above
    [copy setAppHangThresholdMillis:self.appHangThresholdMillis];
    [copy setAppType:self.appType];
    [copy setAppVersion:self.appVersion];
    [copy setAutoDetectErrors:self.autoDetectErrors];
    [copy setAutoTrackSessions:self.autoTrackSessions];
    [copy setBundleVersion:self.bundleVersion];
    [copy setContext:self.context];
    [copy setEnabledBreadcrumbTypes:self.enabledBreadcrumbTypes];
    [copy setEnabledErrorTypes:self.enabledErrorTypes];
    [copy setEnabledReleaseStages:self.enabledReleaseStages];
    copy.discardClasses = self.discardClasses;
    [copy setRedactedKeys:self.redactedKeys];
    [copy setLaunchDurationMillis:self.launchDurationMillis];
    [copy setSendLaunchCrashesSynchronously:self.sendLaunchCrashesSynchronously];
    [copy setMaxPersistedEvents:self.maxPersistedEvents];
    [copy setMaxPersistedSessions:self.maxPersistedSessions];
    [copy setMaxBreadcrumbs:self.maxBreadcrumbs];
    [copy setMetadata:self.metadata];
    [copy setEndpoints:self.endpoints];
    [copy setOnCrashHandler:self.onCrashHandler];
    [copy setPersistUser:self.persistUser];
    [copy setPlugins:[self.plugins copy]];
    [copy setReleaseStage:self.releaseStage];
    copy.session = self.session; // NSURLSession does not declare conformance to NSCopying
    [copy setSendThreads:self.sendThreads];
    [copy setUser:self.user.id
        withEmail:self.user.email
          andName:self.user.name];

    // retain original blocks to allow removing blocks added in config
    // as creating a copy of the array would prevent this
    [copy setOnBreadcrumbBlocks:self.onBreadcrumbBlocks];
    [copy setOnSendBlocks:self.onSendBlocks];
    [copy setOnSessionBlocks:self.onSessionBlocks];
    return copy;
}

// -----------------------------------------------------------------------------
// MARK: - Class Methods
// -----------------------------------------------------------------------------

/**
 * Determine the apiKey-validity of a passed-in string:
 * Exactly 32 hexadecimal digits.
 *
 * @param apiKey The API key.
 * @returns A boolean representing whether the apiKey is valid.
 */
+ (BOOL)isValidApiKey:(NSString *)apiKey {
    NSCharacterSet *chars = [[NSCharacterSet
        characterSetWithCharactersInString:@"0123456789ABCDEF"] invertedSet];

    BOOL isHex = (NSNotFound == [[apiKey uppercaseString] rangeOfCharacterFromSet:chars].location);

    return isHex && [apiKey length] == BSGApiKeyLength;
}

+ (void)setUserDefaults:(NSUserDefaults *)newValue {
    userDefaults = newValue;
}

+ (NSUserDefaults *)userDefaults {
    return userDefaults;
}

// -----------------------------------------------------------------------------
// MARK: - Initializers
// -----------------------------------------------------------------------------

/**
 * Should not be called, but if it _is_ then fail meaningfully rather than silently
 */
- (instancetype)init {
    @throw [NSException exceptionWithName:NSInternalInconsistencyException reason:
            @"-[BugsnagConfiguration init] is unavailable.  Use -[BugsnagConfiguration initWithApiKey:] instead." userInfo:nil];
}

/**
 * The designated initializer.
 */
- (instancetype)initWithApiKey:(NSString *)apiKey {
    if (!(self = [super init])) {
        return nil;
    }
    if (apiKey) {
        [self setApiKey:apiKey];
    }
    _metadata = [[BugsnagMetadata alloc] init];
    _endpoints = [BugsnagEndpointConfiguration new];
    _autoDetectErrors = YES;
    _appHangThresholdMillis = BugsnagAppHangThresholdFatalOnly;
    _onSendBlocks = [NSMutableArray new];
    _onSessionBlocks = [NSMutableArray new];
    _onBreadcrumbBlocks = [NSMutableArray new];
    _plugins = [NSMutableSet new];
    _enabledReleaseStages = nil;
    _redactedKeys = [NSSet setWithArray:@[@"password"]];
    _enabledBreadcrumbTypes = BSGEnabledBreadcrumbTypeAll;
    _launchDurationMillis = 5000;
    _sendLaunchCrashesSynchronously = YES;
    _maxBreadcrumbs = 25;
    _maxPersistedEvents = 32;
    _maxPersistedSessions = 128;
    _autoTrackSessions = YES;
    _sendThreads = BSGThreadSendPolicyAlways;
    // Default to recording all error types
    _enabledErrorTypes = [BugsnagErrorTypes new];

    // Enabling OOM detection only happens in release builds, to avoid triggering
    // the heuristic when killing/restarting an app in Xcode or similar.
    _persistUser = YES;
    // Only gets persisted user data if there is any, otherwise nil
    // persistUser isn't settable until post-init.
    _user = [self getPersistedUserData];

    if ([NSURLSession class]) {
        _session = [NSURLSession
            sessionWithConfiguration:[NSURLSessionConfiguration
                                         defaultSessionConfiguration]];
    }
    
    NSString *releaseStage = nil;
    #if DEBUG
        releaseStage = BSGKeyDevelopment;
    #else
        releaseStage = BSGKeyProduction;
    #endif

    NSString *appType = nil;
    #if BSG_PLATFORM_TVOS
        appType = @"tvOS";
    #elif BSG_PLATFORM_IOS
        appType = @"iOS";
    #elif BSG_PLATFORM_OSX
        appType = @"macOS";
    #else
        appType = @"unknown";
    #endif

    [self setAppType:appType];
    [self setReleaseStage:releaseStage];
    [self setAppVersion:NSBundle.mainBundle.infoDictionary[@"CFBundleShortVersionString"]];
    [self setBundleVersion:NSBundle.mainBundle.infoDictionary[@"CFBundleVersion"]];

    return self;
}

- (instancetype)initWithDictionaryRepresentation:(NSDictionary<NSString *, id> *)dictionaryRepresentation {
    if (!(self = [super init])) {
        return nil;
    }
    _appType = dictionaryRepresentation[BSGKeyAppType];
    _appVersion = dictionaryRepresentation[BSGKeyAppVersion];
    _bundleVersion = dictionaryRepresentation[BSGKeyBundleVersion];
    _context = dictionaryRepresentation[BSGKeyContext];
    _enabledReleaseStages = dictionaryRepresentation[BSGKeyEnabledReleaseStages];
    _releaseStage = dictionaryRepresentation[BSGKeyReleaseStage];
    return self;
}

// -----------------------------------------------------------------------------
// MARK: - Instance Methods
// -----------------------------------------------------------------------------

- (NSDictionary<NSString *, id> *)dictionaryRepresentation {
    NSMutableDictionary *dictionaryRepresentation = [NSMutableDictionary dictionary];
    dictionaryRepresentation[BSGKeyAppType] = self.appType;
    dictionaryRepresentation[BSGKeyAppVersion] = self.appVersion;
    dictionaryRepresentation[BSGKeyBundleVersion] = self.bundleVersion;
    dictionaryRepresentation[BSGKeyContext] = self.context;
    dictionaryRepresentation[BSGKeyEnabledReleaseStages] = self.enabledReleaseStages.allObjects;
    dictionaryRepresentation[BSGKeyReleaseStage] = self.releaseStage;
    return dictionaryRepresentation;
}

/**
 *  Whether reports should be sent, based on release stage options
 *
 *  @return YES if reports should be sent based on this configuration
 */
- (BOOL)shouldSendReports {
    return self.enabledReleaseStages.count == 0 ||
           [self.enabledReleaseStages containsObject:self.releaseStage ?: @""];
}

- (void)setUser:(NSString *_Nullable)userId
      withEmail:(NSString *_Nullable)email
        andName:(NSString *_Nullable)name {
    self.user = [[BugsnagUser alloc] initWithUserId:userId name:name emailAddress:email];

    if (self.persistUser) {
        [self persistUserData];
    }
}

// =============================================================================
// MARK: - onSendBlock
// =============================================================================

- (void)addOnSendErrorBlock:(BugsnagOnSendErrorBlock _Nonnull)block {
    [(NSMutableArray *)self.onSendBlocks addObject:[block copy]];
}

- (void)removeOnSendErrorBlock:(BugsnagOnSendErrorBlock _Nonnull )block
{
    [(NSMutableArray *)self.onSendBlocks removeObject:block];
}

// =============================================================================
// MARK: - onSessionBlock
// =============================================================================

- (void)addOnSessionBlock:(BugsnagOnSessionBlock)block {
    [(NSMutableArray *)self.onSessionBlocks addObject:[block copy]];
}

- (void)removeOnSessionBlock:(BugsnagOnSessionBlock)block {
    [(NSMutableArray *)self.onSessionBlocks removeObject:block];
}

// =============================================================================
// MARK: - onBreadcrumbBlock
// =============================================================================

- (void)addOnBreadcrumbBlock:(BugsnagOnBreadcrumbBlock _Nonnull)block {
    [(NSMutableArray *)self.onBreadcrumbBlocks addObject:[block copy]];
}

- (void)removeOnBreadcrumbBlock:(BugsnagOnBreadcrumbBlock _Nonnull)block {
    [(NSMutableArray *)self.onBreadcrumbBlocks removeObject:block];
}

- (NSDictionary *)sessionApiHeaders {
    return @{BugsnagHTTPHeaderNameApiKey: self.apiKey ?: @"",
             BugsnagHTTPHeaderNamePayloadVersion: @"1.0",
             BugsnagHTTPHeaderNameSentAt: [BSG_RFC3339DateTool stringFromDate:[NSDate date]]
    };
}

- (void)setEndpoints:(BugsnagEndpointConfiguration *)endpoints {
    if ([self isValidURLString:endpoints.notify]) {
        _endpoints.notify = [endpoints.notify copy];
    } else {
        // This causes a crash under DEBUG but is ignored in production
        NSAssert(NO, @"Invalid URL supplied for notify endpoint");
        _endpoints.notify = @"";
    }
    if ([self isValidURLString:endpoints.sessions]) {
        _endpoints.sessions = [endpoints.sessions copy];
    } else {
        bsg_log_err(@"Invalid URL supplied for session endpoint");
        _endpoints.sessions = @"";
    }
}

- (BOOL)isValidURLString:(NSString *)URLString {
    NSURL *url = [NSURL URLWithString:URLString];
    return url != nil && url.scheme != nil && url.host != nil;
}

// MARK: - User Persistence

@synthesize persistUser = _persistUser;

- (BOOL)persistUser {
    @synchronized (self) {
        return _persistUser;
    }
}

- (void)setPersistUser:(BOOL)persistUser {
    @synchronized (self) {
        _persistUser = persistUser;
        if (persistUser) {
            [self persistUserData];
        }
        else {
            [self deletePersistedUserData];
        }
    }
}

/**
 * Retrieve a persisted user, if we have any valid, persisted fields, or nil otherwise
 */
- (BugsnagUser *)getPersistedUserData {
    @synchronized(self) {
        NSString *email = [userDefaults objectForKey:kBugsnagUserEmailAddress];
        NSString *name = [userDefaults objectForKey:kBugsnagUserName];
        NSString *userId = [userDefaults objectForKey:kBugsnagUserUserId];

        if (email || name || userId) {
            return [[BugsnagUser alloc] initWithUserId:userId name:name emailAddress:email];
        } else {
            return [[BugsnagUser alloc] initWithUserId:nil name:nil emailAddress:nil];
        }
    }
}

/**
 * Store user data in a secure location (i.e. the keychain) that persists between application runs
 * 'storing' nil values deletes them.
 */
- (void)persistUserData {
    @synchronized(self) {
        if (self.user) {
            // Email
            if (self.user.email) {
                [userDefaults setObject:self.user.email forKey:kBugsnagUserEmailAddress];
            }
            else {
                [userDefaults removeObjectForKey:kBugsnagUserEmailAddress];
            }

            // Name
            if (self.user.name) {
                [userDefaults setObject:self.user.name forKey:kBugsnagUserName];
            }
            else {
                [userDefaults removeObjectForKey:kBugsnagUserName];
            }

            // UserId
            if (self.user.id) {
                [userDefaults setObject:self.user.id forKey:kBugsnagUserUserId];
            }
            else {
                [userDefaults removeObjectForKey:kBugsnagUserUserId];
            }
        }
    }
}

/**
 * Delete any persisted user data
 */
-(void)deletePersistedUserData {
    @synchronized(self) {
        [userDefaults removeObjectForKey:kBugsnagUserEmailAddress];
        [userDefaults removeObjectForKey:kBugsnagUserName];
        [userDefaults removeObjectForKey:kBugsnagUserUserId];
    }
}

// -----------------------------------------------------------------------------
// MARK: - Properties: Getters and Setters
// -----------------------------------------------------------------------------

- (void)setAppHangThresholdMillis:(NSUInteger)appHangThresholdMillis {
    if (appHangThresholdMillis >= 250) {
        _appHangThresholdMillis = appHangThresholdMillis;
    } else {
        bsg_log_err(@"Invalid configuration value detected. Option appHangThresholdMillis "
                    "should be greater than or equal to 250. Supplied value is %lu",
                    (unsigned long)appHangThresholdMillis);
    }
}

- (void)setMaxPersistedEvents:(NSUInteger)maxPersistedEvents {
    @synchronized (self) {
        if (maxPersistedEvents >= 1) {
            _maxPersistedEvents = maxPersistedEvents;
        } else {
            bsg_log_err(@"Invalid configuration value detected. Option maxPersistedEvents "
                        "should be a non-zero integer. Supplied value is %lu",
                        (unsigned long) maxPersistedEvents);
        }
    }
}

- (void)setMaxPersistedSessions:(NSUInteger)maxPersistedSessions {
    @synchronized (self) {
        if (maxPersistedSessions >= 1) {
            _maxPersistedSessions = maxPersistedSessions;
        } else {
            bsg_log_err(@"Invalid configuration value detected. Option maxPersistedSessions "
                        "should be a non-zero integer. Supplied value is %lu",
                        (unsigned long) maxPersistedSessions);
        }
    }
}

@synthesize maxBreadcrumbs = _maxBreadcrumbs;

- (NSUInteger)maxBreadcrumbs {
    @synchronized (self) {
        return _maxBreadcrumbs;
    }
}

- (void)setMaxBreadcrumbs:(NSUInteger)maxBreadcrumbs {
    @synchronized (self) {
        if (maxBreadcrumbs <= 100) {
            _maxBreadcrumbs = maxBreadcrumbs;
        } else {
            bsg_log_err(@"Invalid configuration value detected. Option maxBreadcrumbs "
                        "should be an integer between 0-100. Supplied value is %lu",
                        (unsigned long) maxBreadcrumbs);
        }
    }
}

- (NSURL *)notifyURL {
    return self.endpoints.notify.length ? [NSURL URLWithString:self.endpoints.notify] : nil;
}

- (NSURL *)sessionURL {
    return self.endpoints.sessions.length ? [NSURL URLWithString:self.endpoints.sessions] : nil;
}

- (BOOL)shouldDiscardErrorClass:(NSString *)errorClass {
    for (id obj in self.discardClasses) {
        if ([obj isKindOfClass:[NSString class]]) {
            if ([obj isEqualToString:errorClass]) {
                return YES;
            }
        } else if ([obj isKindOfClass:[NSRegularExpression class]]) {
            if ([obj firstMatchInString:errorClass options:0 range:NSMakeRange(0, errorClass.length)]) {
                return YES;
            }
        }
    }
    return NO;
}

/**
 * Specific types of breadcrumb should be recorded if either enabledBreadcrumbTypes
 * is None, or contains the type.
 *
 * @param type The breadcrumb type to test
 * @returns Whether to record the breadcrumb
 */
- (BOOL)shouldRecordBreadcrumbType:(BSGBreadcrumbType)type {
    // enabledBreadcrumbTypes is BSGEnabledBreadcrumbTypeNone
    if (self.enabledBreadcrumbTypes == BSGEnabledBreadcrumbTypeNone && type != BSGBreadcrumbTypeManual) {
        return NO;
    }

    switch (type) {
        case BSGBreadcrumbTypeManual:
            return YES;
        case BSGBreadcrumbTypeError :
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeError) != 0;
        case BSGBreadcrumbTypeLog:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeLog) != 0;
        case BSGBreadcrumbTypeNavigation:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeNavigation) != 0;
        case BSGBreadcrumbTypeProcess:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeProcess) != 0;
        case BSGBreadcrumbTypeRequest:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeRequest) != 0;
        case BSGBreadcrumbTypeState:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeState) != 0;
        case BSGBreadcrumbTypeUser:
            return (self.enabledBreadcrumbTypes & BSGEnabledBreadcrumbTypeUser) != 0;
    }
    return NO;
}

// MARK: - enabledBreadcrumbTypes

@synthesize enabledBreadcrumbTypes = _enabledBreadcrumbTypes;

- (BSGEnabledBreadcrumbType)enabledBreadcrumbTypes {
    @synchronized (self) {
        return _enabledBreadcrumbTypes;
    }
}

- (void)setEnabledBreadcrumbTypes:(BSGEnabledBreadcrumbType)enabledBreadcrumbTypes {
    @synchronized (self) {
        _enabledBreadcrumbTypes = enabledBreadcrumbTypes;
    }
}

// MARK: -

- (void)validate {
    if (self.apiKey.length == 0) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:
                @"No Bugsnag API key has been provided" userInfo:nil];
    }

    if (![BugsnagConfiguration isValidApiKey:self.apiKey]) {
        bsg_log_warn(@"Invalid apiKey: expected a 32-character hexademical string, got \"%@\"", self.apiKey);
    }
}

// MARK: -

- (void)addPlugin:(id<BugsnagPlugin> _Nonnull)plugin {
    [self.plugins addObject:plugin];
}

// MARK: - <MetadataStore>

- (void)addMetadata:(NSDictionary *_Nonnull)metadata
          toSection:(NSString *_Nonnull)sectionName
{
    [self.metadata addMetadata:metadata toSection:sectionName];
}

- (void)addMetadata:(id _Nullable)metadata
            withKey:(NSString *_Nonnull)key
          toSection:(NSString *_Nonnull)sectionName
{
    [self.metadata addMetadata:metadata withKey:key toSection:sectionName];
}

- (id _Nullable)getMetadataFromSection:(NSString *_Nonnull)sectionName
                               withKey:(NSString *_Nonnull)key
{
    return [self.metadata getMetadataFromSection:sectionName withKey:key];
}

- (NSMutableDictionary *)getMetadataFromSection:(NSString *_Nonnull)sectionName
{
    return [self.metadata getMetadataFromSection:sectionName];
}

- (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
{
    [self.metadata clearMetadataFromSection:sectionName];
}

- (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
                       withKey:(NSString *_Nonnull)key
{
    [self.metadata clearMetadataFromSection:sectionName withKey:key];
}

@end
