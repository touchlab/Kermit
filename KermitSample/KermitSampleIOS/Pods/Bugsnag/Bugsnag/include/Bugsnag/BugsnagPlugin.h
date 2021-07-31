#ifndef BugsnagPlugin_h
#define BugsnagPlugin_h

#import <Foundation/Foundation.h>

@class Bugsnag;
@class BugsnagClient;

/**
 * Internal interface for adding custom behavior :nodoc:
 */
@protocol BugsnagPlugin <NSObject>

@required

/**
 * Loads a plugin with the given Client. When this method is invoked the plugin should
 * activate its behaviour - for example, by capturing an additional source of errors.
*/
- (void)load:(BugsnagClient *_Nonnull)client;

/**
 * Unloads a plugin. When this is invoked the plugin should cease all custom behaviour and
 * restore the application to its unloaded state.
 */
- (void)unload;

@end

#endif /* BugsnagPlugin_h */
