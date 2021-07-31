//
//  BugsnagPluginClient.h
//  Bugsnag
//
//  Created by Jamie Lynch on 12/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "BugsnagPlugin.h"

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagPluginClient : NSObject

- (instancetype _Nonnull)initWithPlugins:(NSMutableSet<id<BugsnagPlugin>> *_Nonnull)plugins
                                  client:(BugsnagClient *)client;
- (void)loadPlugins;

@end

NS_ASSUME_NONNULL_END
