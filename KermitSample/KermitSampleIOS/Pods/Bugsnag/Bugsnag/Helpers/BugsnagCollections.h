//
//  Copyright (c) 2016 Bugsnag, Inc. All rights reserved.
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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

// MARK: NSArray

/// Returns an array with the object, or an empty array if object is nil.
NSArray * BSGArrayWithObject(id _Nullable object);

void BSGArrayAddIfNonnull(NSMutableArray *array, id _Nullable object);

/// Returns an array containing the results of mapping the given block over the array's elements
NSArray * BSGArrayMap(NSArray * _Nullable array, id _Nullable (^ transform)(id value));

/// Returns a new array containing the elements starting at position `index`, or
/// an empty array if `index` is beyond the array's range range of elements.
NSArray * BSGArraySubarrayFromIndex(NSArray *array, NSUInteger index);

// MARK: - NSDictionary

/// Returns a dictionary containing the key and object, or an empty dictionary if the object is nil.
NSDictionary * BSGDictionaryWithKeyAndObject(NSString *key, id _Nullable object);

/**
 *  Merge values from source dictionary with destination
 *
 *  @param source a dictionary
 *  @param destination a dictionary or nil
 */
NSDictionary *BSGDictMerge(NSDictionary *source, NSDictionary *destination);

/// Returns a representation of the dictionary that contains only valid JSON.
/// Any dictionary keys that are not strings will be ignored.
/// Any values that are not valid JSON will be replaced by a string description.
NSDictionary * BSGJSONDictionary(NSDictionary *dictionary);

// MARK: - NSSet

void BSGSetAddIfNonnull(NSMutableSet *array, id _Nullable object);

// MARK: - Deserialization

NSDictionary * _Nullable BSGDeserializeDict(id _Nullable rawValue);

id _Nullable BSGDeserializeObject(id _Nullable rawValue, id _Nullable (^ deserializer)(NSDictionary * _Nonnull dict));

id _Nullable BSGDeserializeArrayOfObjects(id _Nullable rawValue, id _Nullable (^ deserializer)(NSDictionary * _Nonnull dict));

NSString * _Nullable BSGDeserializeString(id _Nullable rawValue);

NSDate * _Nullable BSGDeserializeDate(id _Nullable rawValue);

NSNumber * _Nullable BSGDeserializeNumber(id  _Nullable rawValue);

NS_ASSUME_NONNULL_END
