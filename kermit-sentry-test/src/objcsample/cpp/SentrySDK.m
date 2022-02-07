#import "SentrySDK.h"

@implementation SentrySDK

    + (SentryId *)captureException:(NSException *)exception NS_SWIFT_NAME(capture(exception:)) {
        return nil;
    }

    + (void)addBreadcrumb:(SentryBreadcrumb *)crumb NS_SWIFT_NAME(addBreadcrumb(crumb:)) {

    }

@end