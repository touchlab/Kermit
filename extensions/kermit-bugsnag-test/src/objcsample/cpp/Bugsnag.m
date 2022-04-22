//
// Created by Kevin Galligan on 10/30/21.
//

#import "Bugsnag.h"


@implementation Bugsnag
- (instancetype)init {
    return nil;
}

+ (void)notify:(NSException *)exception {

}

+ (void)leaveBreadcrumbWithMessage:(NSString *)message {

}

+ (void)leaveBreadcrumbWithMessage:(NSString *)message metadata:(NSDictionary *)metadata andType:(BSGBreadcrumbType)type {

}

@end