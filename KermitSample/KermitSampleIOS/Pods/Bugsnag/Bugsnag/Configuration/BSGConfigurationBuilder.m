#import "BSGConfigurationBuilder.h"

#import "BugsnagConfiguration.h"
#import "BugsnagEndpointConfiguration.h"
#import "BugsnagKeys.h"
#import "BugsnagLogger.h"

static BOOL BSGValueIsBoolean(id object) {
    return object != nil && [object isKindOfClass:[NSNumber class]]
            && CFGetTypeID((__bridge CFTypeRef)object) == CFBooleanGetTypeID();
}

@implementation BSGConfigurationBuilder

+ (BugsnagConfiguration *)configurationFromOptions:(NSDictionary *)options {
    NSString *apiKey = options[BSGKeyApiKey];
    if (apiKey != nil && ![apiKey isKindOfClass:[NSString class]]) {
        @throw [NSException exceptionWithName:NSInvalidArgumentException reason:@"Bugsnag apiKey must be a string" userInfo:nil];
    }

    BugsnagConfiguration *config = [[BugsnagConfiguration alloc] initWithApiKey:apiKey];

    NSArray<NSString *> *validKeys = @[
        BSGKeyApiKey,
        BSGKeyAppType,
        BSGKeyAppVersion,
        BSGKeyAutoDetectErrors,
        BSGKeyAutoTrackSessions,
        BSGKeyBundleVersion,
        BSGKeyEnabledReleaseStages,
        BSGKeyEndpoints,
        BSGKeyMaxBreadcrumbs,
        BSGKeyMaxPersistedEvents,
        BSGKeyMaxPersistedSessions,
        BSGKeyPersistUser,
        BSGKeyRedactedKeys,
        BSGKeyReleaseStage,
        BSGKeySendThreads,
    ];
    
    NSMutableSet *unknownKeys = [NSMutableSet setWithArray:options.allKeys];
    [unknownKeys minusSet:[NSSet setWithArray:validKeys]];
    if (unknownKeys.count > 0) {
        bsg_log_warn(@"Unknown dictionary keys passed in configuration options: %@", unknownKeys);
    }
    
    [self loadString:config options:options key:BSGKeyAppType];
    [self loadString:config options:options key:BSGKeyAppVersion];
    [self loadBoolean:config options:options key:BSGKeyAutoDetectErrors];
    [self loadBoolean:config options:options key:BSGKeyAutoTrackSessions];
    [self loadString:config options:options key:BSGKeyBundleVersion];
    [self loadBoolean:config options:options key:BSGKeyPersistUser];
    [self loadString:config options:options key:BSGKeyReleaseStage];

    [self loadStringArray:config options:options key:BSGKeyEnabledReleaseStages];
    [self loadStringArray:config options:options key:BSGKeyRedactedKeys];
    [self loadEndpoints:config options:options];

    [self loadNumber:config options:options key:BSGKeyMaxBreadcrumbs];
    [self loadNumber:config options:options key:BSGKeyMaxPersistedEvents];
    [self loadNumber:config options:options key:BSGKeyMaxPersistedSessions];
    [self loadSendThreads:config options:options];
    return config;
}

+ (void)loadBoolean:(BugsnagConfiguration *)config options:(NSDictionary *)options key:(NSString *)key {
    if (BSGValueIsBoolean(options[key])) {
        [config setValue:options[key] forKey:key];
    }
}

+ (void)loadString:(BugsnagConfiguration *)config options:(NSDictionary *)options key:(NSString *)key {
    if (options[key] && [options[key] isKindOfClass:[NSString class]]) {
        [config setValue:options[key] forKey:key];
    }
}

+ (void)loadNumber:(BugsnagConfiguration *)config options:(NSDictionary *)options key:(NSString *)key {
    if (options[key] && [options[key] isKindOfClass:[NSNumber class]]) {
        [config setValue:options[key] forKey:key];
    }
}

+ (void)loadStringArray:(BugsnagConfiguration *)config options:(NSDictionary *)options key:(NSString *)key {
    if (options[key] && [options[key] isKindOfClass:[NSArray class]]) {
        NSArray *val = options[key];

        for (NSString *obj in val) {
            if (![obj isKindOfClass:[NSString class]]) {
                return;
            }
        }
        [config setValue:val forKey:key];
    }
}

+ (void)loadEndpoints:(BugsnagConfiguration *)config options:(NSDictionary *)options {
    if (options[BSGKeyEndpoints] && [options[BSGKeyEndpoints] isKindOfClass:[NSDictionary class]]) {
        NSDictionary *endpoints = options[BSGKeyEndpoints];

        NSString *notify = endpoints[BSGKeyNotifyEndpoint];
        if ([notify isKindOfClass:[NSString class]]) {
            config.endpoints.notify = notify;
        }
        NSString *sessions = endpoints[BSGKeySessionsEndpoint];
        if ([sessions isKindOfClass:[NSString class]]) {
            config.endpoints.sessions = sessions;
        }
    }
}

+ (void)loadSendThreads:(BugsnagConfiguration *)config options:(NSDictionary *)options {
    if (options[BSGKeySendThreads] && [options[BSGKeySendThreads] isKindOfClass:[NSString class]]) {
        NSString *sendThreads = [options[BSGKeySendThreads] lowercaseString];

        if ([@"unhandledonly" isEqualToString:sendThreads]) {
            config.sendThreads = BSGThreadSendPolicyUnhandledOnly;
        } else if ([@"always" isEqualToString:sendThreads]) {
            config.sendThreads = BSGThreadSendPolicyAlways;
        } else if ([@"never" isEqualToString:sendThreads]) {
            config.sendThreads = BSGThreadSendPolicyNever;
        }
    }
}

@end
