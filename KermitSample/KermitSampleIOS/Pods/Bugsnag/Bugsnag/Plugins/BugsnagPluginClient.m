//
//  BugsnagPluginClient.m
//  Bugsnag
//
//  Created by Jamie Lynch on 12/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import "Bugsnag.h"
#import "BugsnagPluginClient.h"
#import "BugsnagPlugin.h"
#import "BugsnagLogger.h"

static NSString *const kPluginReactNative = @"BugsnagReactNativePlugin";

@interface BugsnagPluginClient ()
@property (nonatomic) NSSet<id<BugsnagPlugin>> *plugins;
@property (nonatomic) BugsnagClient *client;
@end

@implementation BugsnagPluginClient

- (instancetype _Nonnull)initWithPlugins:(NSMutableSet<id<BugsnagPlugin>> *_Nonnull)plugins
                                  client:(BugsnagClient *)client {
    if ((self = [super init])) {
        NSMutableSet *instantiatedPlugins = [plugins mutableCopy];
        id rnPlugin = [self instantiateBugsnagPlugin:kPluginReactNative];

        if (rnPlugin) {
            [instantiatedPlugins addObject:rnPlugin];
        }
        _plugins = [NSSet setWithSet:instantiatedPlugins];
        _client = client;
    }
    return self;
}

/**
 * Automagically instantiate plugins which Bugsnag uses via class name (e.g. BugsnagReactNativePlugin)
 */
- (instancetype)instantiateBugsnagPlugin:(NSString *)clzName {
    Class clz = NSClassFromString(clzName);
    if (clz) {
        return [clz new];
    }
    return nil;
}

- (void)loadPlugins {
    for (id<BugsnagPlugin> plugin in self.plugins) {
        @try {
            [plugin load:self.client];
        } @catch (NSException *exception) {
            bsg_log_err(@"Failed to load plugin %@, continuing with initialisation.", plugin);
        }
    }
}

@end
