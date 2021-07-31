//
//  BugsnagKVStoreObjC.h
//  Bugsnag
//
//  Created by Karl Stenerud on 15.09.20.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * BugsnagKVStore is an objective-c wrapper around the C version of the KV store.
 * All instances of BugsnagKVStore share the same underlying C implementation, initialized
 * to the same directory under the caches directory. If in future we need multiple KV stores,
 * this will have to be changed. For now, it's simplest to only have one.
 */
@interface BugsnagKVStore : NSObject

/**
 * Purge the KV store, deleting all values.
 */
- (void)purge;

- (void)deleteKey:(NSString *)key;

- (void)setBoolean:(bool)value forKey:(NSString*)key;

- (bool)booleanForKey:(NSString*)key defaultValue:(bool)defaultValue;

- (nullable NSNumber*)NSBooleanForKey:(NSString*)key defaultValue:(nullable NSNumber*)defaultValue;

- (void)setString:(NSString*)value forKey:(NSString*)key;

- (NSString*)stringForKey:(NSString*)key defaultValue:(NSString*)defaultValue;

// Note: Other types such as float and bytes can be added later if needed.

@end

NS_ASSUME_NONNULL_END
