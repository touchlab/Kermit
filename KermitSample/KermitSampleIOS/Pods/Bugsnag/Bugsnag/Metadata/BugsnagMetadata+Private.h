//
//  BugsnagMetadata+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagMetadata.h"

@class BugsnagStateEvent;

NS_ASSUME_NONNULL_BEGIN

typedef void (^BugsnagObserverBlock)(BugsnagStateEvent *event);

@interface BugsnagMetadata ()

#pragma mark Properties

@property (readonly, nonatomic) NSMutableDictionary *dictionary;

#pragma mark Methods

- (NSDictionary *)toDictionary;

- (instancetype)deepCopy;

- (void)addObserverWithBlock:(BugsnagObserverBlock)block;

- (void)removeObserverWithBlock:(BugsnagObserverBlock)block;

@end

NS_ASSUME_NONNULL_END
