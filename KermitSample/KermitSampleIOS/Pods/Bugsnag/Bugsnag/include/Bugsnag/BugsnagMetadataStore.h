//
//  BugsnagMetadataStore.h
//  Bugsnag
//
//  Created by Robin Macharg on 30/03/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
* Metadata allows semi-arbitrary data to be supplied by the developer.
* It is a set of named sections containing key value pairs, where the
* values can be of any type.
*/

// -----------------------------------------------------------------------------
// MARK: - <BugsnagMetadataStore>
// -----------------------------------------------------------------------------

/**
 * An internal protocol defining methods a Bugsnag metadata store must implement.
 */
@protocol BugsnagMetadataStore <NSObject>

@required

/**
 * Merge supplied and existing metadata.
 *
 * - Non-null values will replace existing values for identical keys.
 *
 * - Null values will remove the existing key/value pair if the key exists.
 *   Where null-valued keys do not exist they will not be set.  (Since ObjC
 *   dicts can't store 'nil' directly we assume [NSNUll null])
 *
 * - Tabs are only created if at least one value is valid.
 *
 * - Invalid values (i.e. unserializable to JSON) are logged and ignored.
 *
 * @param metadata A dictionary of string -> id key/value pairs.
 *                 Values should be serializable to JSON.
 *
 * @param sectionName The name of the metadata section
 *
 */
- (void)addMetadata:(NSDictionary *_Nonnull)metadata
          toSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(addMetadata(_:section:));

/**
 * Add a piece of metadata to a particular key in a particular section.
 *
 * - Non-null values will replace existing values for identical keys.
 *
 * - Null values will remove the existing key/value pair if the key exists.
 *   Where null-valued keys do not exist they will not be set.  (Since ObjC
 *   dicts can't store 'nil' directly we assume [NSNUll null])
 *
 * - Tabs are only created if at least one value is valid.
 *
 * - Invalid values (i.e. unserializable to JSON) are logged and ignored.
 *
 * @param metadata A dictionary of string -> id key/value pairs.
 *                 Values should be serializable to JSON.
 *
 * @param key The metadata key to store the value under
 *
 * @param sectionName The name of the metadata section
 *
 */
- (void)addMetadata:(id _Nullable)metadata
            withKey:(NSString *_Nonnull)key
          toSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(addMetadata(_:key:section:));

/**
 * Get a named metadata section
 *
 * @param sectionName The name of the section
 * @returns The mutable dictionary representing the metadata section, if it
 *          exists, or nil if not.
 */
- (NSMutableDictionary *_Nullable)getMetadataFromSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(getMetadata(section:));

/**
 * Get a keyed value from a named metadata section
 *
 * @param sectionName The name of the section
 * @param key The key
 * @returns The value if it exists, or nil if not.
 */
- (id _Nullable)getMetadataFromSection:(NSString *_Nonnull)sectionName
                               withKey:(NSString *_Nonnull)key
    NS_SWIFT_NAME(getMetadata(section:key:));

/**
 * Remove a named metadata section, if it exists.
 *
 * @param sectionName The section name
 */
- (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(clearMetadata(section:));

/**
 * Remove a specific value for a specific key in a specific metadata section.
 * If either section or key do not exist no action is taken.
 *
 * @param sectionName The section name
 * @param key the metadata key
 */
- (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
                         withKey:(NSString *_Nonnull)key
    NS_SWIFT_NAME(clearMetadata(section:key:));

@end

// -----------------------------------------------------------------------------
// MARK: - <BugsnagClassLevelMetadataStore>
// -----------------------------------------------------------------------------

/**
 * A class-level protocol supporting the MetadataStore interface
 */
@protocol BugsnagClassLevelMetadataStore <NSObject>

@required

/**
 * Merge supplied and existing metadata.
 *
 * - Non-null values will replace existing values for identical keys.
 *
 * - Null values will remove the existing key/value pair if the key exists.
 *   Where null-valued keys do not exist they will not be set.  (Since ObjC
 *   dicts can't store 'nil' directly we assume [NSNUll null])
 *
 * - Tabs are only created if at least one value is valid.
 *
 * - Invalid values (i.e. unserializable to JSON) are logged and ignored.
 *
 * @param metadata A dictionary of string -> id key/value pairs.
 *                 Values should be serializable to JSON.
 *
 * @param sectionName The name of the metadata section
 *
 */
+ (void)addMetadata:(NSDictionary *_Nonnull)metadata
          toSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(addMetadata(_:section:));

/**
 * Add a piece of metadata to a particular key in a particular section.
 *
 * - Non-null values will replace existing values for identical keys.
 *
 * - Null values will remove the existing key/value pair if the key exists.
 *   Where null-valued keys do not exist they will not be set.  (Since ObjC
 *   dicts can't store 'nil' directly we assume [NSNUll null])
 *
 * - Tabs are only created if at least one value is valid.
 *
 * - Invalid values (i.e. unserializable to JSON) are logged and ignored.
 *
 * @param metadata A dictionary of string -> id key/value pairs.
 *                 Values should be serializable to JSON.
 *
 * @param key The metadata key to store the value under
 *
 * @param sectionName The name of the metadata section
 *
 */
+ (void)addMetadata:(id _Nullable)metadata
            withKey:(NSString *_Nonnull)key
          toSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(addMetadata(_:key:section:));

/**
 * Get a named metadata section
 *
 * @param sectionName The name of the section
 * @returns The mutable dictionary representing the metadata section, if it
 *          exists, or nil if not.
 */
+ (NSMutableDictionary *_Nullable)getMetadataFromSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(getMetadata(section:));

/**
 * Get a keyed value from a named metadata section
 *
 * @param sectionName The name of the section
 * @param key The key
 * @returns The value if it exists, or nil if not.
 */
+ (id _Nullable)getMetadataFromSection:(NSString *_Nonnull)sectionName
                               withKey:(NSString *_Nonnull)key
    NS_SWIFT_NAME(getMetadata(section:key:));

/**
 * Remove a named metadata section, if it exists.
 *
 * @param sectionName The section name
 */
+ (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
    NS_SWIFT_NAME(clearMetadata(section:));

/**
 * Remove a specific value for a specific key in a specific metadata section.
 * If either section or key do not exist no action is taken.
 *
 * @param sectionName The section name
 * @param key the metadata key
 */
+ (void)clearMetadataFromSection:(NSString *_Nonnull)sectionName
                         withKey:(NSString *_Nonnull)key
    NS_SWIFT_NAME(clearMetadata(section:key:));

@end

NS_ASSUME_NONNULL_END
