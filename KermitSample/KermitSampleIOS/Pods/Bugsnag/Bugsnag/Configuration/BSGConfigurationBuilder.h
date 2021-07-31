#import <Foundation/Foundation.h>

@class BugsnagConfiguration;

@interface BSGConfigurationBuilder : NSObject

/**
 * Creates a configuration, applying supported options before returning the new
 * config object.
 *
 * @return a new BugsnagConfiguration object or nil if the a valid object could
 * not be created (including a non-empty API key)
 */
+ (BugsnagConfiguration *_Nonnull)configurationFromOptions:(NSDictionary *_Nonnull)options;

@end
