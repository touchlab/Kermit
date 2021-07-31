//
//  BugsnagLastRunInfo+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 10/02/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#include "BugsnagLastRunInfo.h"

@interface BugsnagLastRunInfo ()

- (instancetype)initWithConsecutiveLaunchCrashes:(NSUInteger)consecutiveLaunchCrashes
                                         crashed:(BOOL)crashed
                             crashedDuringLaunch:(BOOL)crashedDuringLaunch;

@end
