//
//  BSGEventUploadFileOperation.h
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadOperation.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * A concrete operation class for uploading an event that is stored on disk.
 */
@interface BSGEventUploadFileOperation : BSGEventUploadOperation

- (instancetype)initWithFile:(NSString *)file delegate:(id<BSGEventUploadOperationDelegate>)delegate;

@property (copy, nonatomic) NSString *file;

@end

NS_ASSUME_NONNULL_END
