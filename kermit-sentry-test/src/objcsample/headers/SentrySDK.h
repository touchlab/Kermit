#import <Foundation/Foundation.h>

#import "SentryDefines.h"

@protocol SentrySpan;

@class SentryHub, SentryOptions, SentryEvent, SentryBreadcrumb, SentryScope, SentryUser, SentryId,
    SentryUserFeedback, SentryTransactionContext;

NS_ASSUME_NONNULL_BEGIN

/**
 * The main entry point for the SentrySDK.
 *
 * We recommend using `[Sentry startWithConfigureOptions]` to initialize Sentry.
 */
@interface SentrySDK : NSObject
SENTRY_NO_INIT

/**
 * Captures an exception event and sends it to Sentry.
 *
 * @param exception The exception to send to Sentry.
 *
 * @return The SentryId of the event or SentryId.empty if the event is not sent.
 */
+ (SentryId *)captureException:(NSException *)exception NS_SWIFT_NAME(capture(exception:));

/**
 * Adds a Breadcrumb to the current Scope of the current Hub. If the total number of breadcrumbs
 * exceeds the `SentryOptions.maxBreadcrumbs`, the SDK removes the oldest breadcrumb.
 *
 * @param crumb The Breadcrumb to add to the current Scope of the current Hub.
 */
+ (void)addBreadcrumb:(SentryBreadcrumb *)crumb NS_SWIFT_NAME(addBreadcrumb(crumb:));

@end

NS_ASSUME_NONNULL_END
