//
//  BSGJSONSerialization.h
//  Bugsnag
//
//  Created by Karl Stenerud on 03.09.20.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/* Wraps NSJSONSerialization to trap any exceptions and return them as errors.
 *
 * NSJSONSerialization sometimes returns errors and sometimes throws exceptions,
 * with no specification as to which mechanism will trigger for what kinds of errors.
 * This wrapper catches all exceptions and forces everything to be returned as an error.
 */
@interface BSGJSONSerialization : NSObject

/* Returns YES if the given object can be converted to JSON data, NO otherwise. The object must have the following properties:
    - Top level object is an NSArray or NSDictionary
    - All objects are NSString, NSNumber, NSArray, NSDictionary, or NSNull
    - All dictionary keys are NSStrings
    - NSNumbers are not NaN or infinity
 Other rules may apply. Calling this method or attempting a conversion are the definitive ways to tell if a given object can be converted to JSON data.
 */
+ (BOOL)isValidJSONObject:(nullable id)obj;

/* Generate JSON data from a Foundation object. If the object will not produce valid JSON then an error will be returned. Setting the NSJSONWritingPrettyPrinted option will generate JSON with whitespace designed to make the output more readable. If that option is not set, the most compact possible JSON will be generated. If an error occurs, the error parameter will be set and the return value will be nil. The resulting data is a encoded in UTF-8.
 */
+ (nullable NSData *)dataWithJSONObject:(id)obj options:(NSJSONWritingOptions)opt error:(NSError **)error;

/* Create a Foundation object from JSON data. Set the NSJSONReadingAllowFragments option if the parser should allow top-level objects that are not an NSArray or NSDictionary. Setting the NSJSONReadingMutableContainers option will make the parser generate mutable NSArrays and NSDictionaries. Setting the NSJSONReadingMutableLeaves option will make the parser generate mutable NSString objects. If an error occurs during the parse, then the error parameter will be set and the result will be nil.
   The data must be in one of the 5 supported encodings listed in the JSON specification: UTF-8, UTF-16LE, UTF-16BE, UTF-32LE, UTF-32BE. The data may or may not have a BOM. The most efficient encoding to use for parsing is UTF-8, so if you have a choice in encoding the data passed to this method, use UTF-8.
 */
+ (nullable id)JSONObjectWithData:(NSData *)data options:(NSJSONReadingOptions)opt error:(NSError **)error;

/* Write JSON data into a stream. The stream should be opened and configured. The return value is the number of bytes written to the stream, or 0 on error. All other behavior of this method is the same as the dataWithJSONObject:options:error: method.
 */
+ (NSInteger)writeJSONObject:(id)obj toStream:(NSOutputStream *)stream options:(NSJSONWritingOptions)opt error:(NSError **)error;

/* Create a JSON object from JSON data stream. The stream should be opened and configured. All other behavior of this method is the same as the JSONObjectWithData:options:error: method.
 */
+ (nullable id)JSONObjectWithStream:(NSInputStream *)stream options:(NSJSONReadingOptions)opt error:(NSError **)error;

+ (BOOL)writeJSONObject:(id)JSONObject toFile:(NSString *)file options:(NSJSONWritingOptions)options error:(NSError **)errorPtr;

+ (nullable id)JSONObjectWithContentsOfFile:(NSString *)file options:(NSJSONReadingOptions)options error:(NSError **)errorPtr;

@end

NS_ASSUME_NONNULL_END
