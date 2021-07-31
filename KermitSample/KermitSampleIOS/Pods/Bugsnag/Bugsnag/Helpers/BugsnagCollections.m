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

#import "BugsnagCollections.h"

#import "BSG_RFC3339DateTool.h"
#import "BSGJSONSerialization.h"

// MARK: NSArray

NSArray * BSGArrayWithObject(id _Nullable object) {
    return object ? @[(id _Nonnull)object] : @[];
}

void BSGArrayAddIfNonnull(NSMutableArray *array, id _Nullable object) {
    if (object) {
        [array addObject:(id _Nonnull)object];
    }
}

NSArray * BSGArrayMap(NSArray *array, id _Nullable (^ transform)(id)) {
    NSMutableArray *mappedArray = [NSMutableArray array];
    for (id object in array) {
        id mapped = transform(object);
        if (mapped) {
            [mappedArray addObject:mapped];
        }
    }
    return mappedArray;
}

NSArray * BSGArraySubarrayFromIndex(NSArray *array, NSUInteger index) {
    if (index >= array.count) {
        return @[];
    }
    return [array subarrayWithRange:NSMakeRange(index, array.count - index)];
}

// MARK: - NSDictionary

NSDictionary * BSGDictionaryWithKeyAndObject(NSString *key, id _Nullable object) {
    return object ? @{key: (id _Nonnull)object} : @{};
}

NSDictionary *BSGDictMerge(NSDictionary *source, NSDictionary *destination) {
    if ([destination count] == 0) {
        return source;
    }
    if ([source count] == 0) {
        return destination;
    }
    
    NSMutableDictionary *dict = [destination mutableCopy];
    for (id key in [source allKeys]) {
        id srcEntry = source[key];
        id dstEntry = destination[key];
        if ([dstEntry isKindOfClass:[NSDictionary class]] &&
            [srcEntry isKindOfClass:[NSDictionary class]]) {
            srcEntry = BSGDictMerge(srcEntry, dstEntry);
        }
        dict[key] = srcEntry;
    }
    return dict;
}

NSDictionary * BSGJSONDictionary(NSDictionary *dictionary) {
    if (!dictionary) {
        return nil;
    }
    if ([BSGJSONSerialization isValidJSONObject:dictionary]) {
        return dictionary;
    }
    NSMutableDictionary *json = [NSMutableDictionary dictionary];
    for (id key in dictionary) {
        if (![key isKindOfClass:[NSString class]]) {
            continue;
        }
        const id value = dictionary[key];
        if ([BSGJSONSerialization isValidJSONObject:@{key: value}]) {
            json[key] = value;
        } else if ([value isKindOfClass:[NSDictionary class]]) {
            json[key] = BSGJSONDictionary(value);
        } else {
            json[key] = ((NSObject *)value).description;
        }
    }
    return json;
}

// MARK: - NSSet

void BSGSetAddIfNonnull(NSMutableSet *set, id _Nullable object) {
    if (object) {
        [set addObject:(id _Nonnull)object];
    }
}

// MARK: - Deserialization

NSDictionary * _Nullable BSGDeserializeDict(id _Nullable rawValue) {
    if (![rawValue isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    return (NSDictionary *)rawValue;
}

id _Nullable BSGDeserializeObject(id _Nullable rawValue, id _Nullable (^ deserializer)(NSDictionary * _Nonnull dict)) {
    if (![rawValue isKindOfClass:[NSDictionary class]]) {
        return nil;
    }
    return deserializer((NSDictionary *)rawValue);
}

id _Nullable BSGDeserializeArrayOfObjects(id _Nullable rawValue, id _Nullable (^ deserializer)(NSDictionary * _Nonnull dict)) {
    if (![rawValue isKindOfClass:[NSArray class]]) {
        return nil;
    }
    return BSGArrayMap((NSArray *)rawValue, ^id _Nullable(id _Nonnull value) {
        return BSGDeserializeObject(value, deserializer);
    });
}

NSString * _Nullable BSGDeserializeString(id _Nullable rawValue) {
    if (![rawValue isKindOfClass:[NSString class]]) {
        return nil;
    }
    return (NSString *)rawValue;
}

NSDate * _Nullable BSGDeserializeDate(id _Nullable rawValue) {
    if (![rawValue isKindOfClass:[NSString class]]) {
        return nil;
    }
    return [BSG_RFC3339DateTool dateFromString:(NSString *)rawValue];
}

NSNumber * _Nullable BSGDeserializeNumber(id  _Nullable rawValue) {
    if (![rawValue isKindOfClass:[NSNumber class]]) {
        return nil;
    }
    return (NSNumber *)rawValue;
}
