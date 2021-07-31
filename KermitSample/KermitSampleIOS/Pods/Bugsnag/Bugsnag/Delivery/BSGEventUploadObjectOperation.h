//
//  BSGEventUploadObjectOperation.h
//  Bugsnag
//
//  Created by Nick Dowell on 17/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGEventUploadOperation.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * A concrete operation class for uploading an event object in memory.
 *
 * If the upload needs to be retried, the event will be persisted to disk.
 */
@interface BSGEventUploadObjectOperation : BSGEventUploadOperation

- (instancetype)initWithEvent:(BugsnagEvent *)event delegate:(id<BSGEventUploadOperationDelegate>)delegate;

@property (nonatomic) BugsnagEvent *event;

@end

NS_ASSUME_NONNULL_END
