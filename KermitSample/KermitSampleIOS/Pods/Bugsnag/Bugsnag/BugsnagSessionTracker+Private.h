//
//  BugsnagSessionTracker+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 04/12/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import "BugsnagSessionTracker.h"

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagSessionTracker ()

#pragma mark Properties

@property (copy, nonatomic) NSString *codeBundleId;

@property (nullable, nonatomic) BugsnagSession *currentSession;

@end

NS_ASSUME_NONNULL_END
