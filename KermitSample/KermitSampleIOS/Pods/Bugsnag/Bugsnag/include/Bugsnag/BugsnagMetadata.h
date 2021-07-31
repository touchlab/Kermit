//
//  BugsnagMetaData.h
//
//  Created by Conrad Irwin on 2014-10-01.
//
//  Copyright (c) 2014 Bugsnag, Inc. All rights reserved.
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
//

#import <Foundation/Foundation.h>

#import <Bugsnag/BugsnagMetadataStore.h>

NS_ASSUME_NONNULL_BEGIN

/// :nodoc:
@interface BugsnagMetadata : NSObject <BugsnagMetadataStore>

- (instancetype)initWithDictionary:(NSDictionary *)dict;

/// Configures the metadata object to serialize itself to the provided buffer and file immediately, and upon each change.
- (void)setStorageBuffer:(char *_Nullable *_Nullable)buffer file:(nullable NSString *)file;

/// Exposed to facilitate unit testing.
- (void)writeData:(NSData *)data toBuffer:(char *_Nullable *_Nonnull)buffer;

@end

NS_ASSUME_NONNULL_END
