//
//  BugsnagError.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

@class BugsnagStackframe;

/**
 * Denote which platform or runtime the Error occurred in.
 */
typedef NS_OPTIONS(NSUInteger, BSGErrorType) {
    BSGErrorTypeCocoa NS_SWIFT_NAME(cocoa), // Swift won't bring in the zeroeth option by default
    BSGErrorTypeC NS_SWIFT_NAME(c), // Fix Swift auto-capitalisation
    BSGErrorTypeReactNativeJs
};

/**
 * An Error represents information extracted from an NSError, NSException, or other error source.
 */
@interface BugsnagError : NSObject

/**
 * The class of the error generating the report
 */
@property (copy, nullable, nonatomic) NSString *errorClass;

/**
 * The message of or reason for the error generating the report
 */
@property (copy, nullable, nonatomic) NSString *errorMessage;

/**
 * Sets a representation of this error's stacktrace
 */
@property (copy, nonnull, nonatomic) NSArray<BugsnagStackframe *> *stacktrace;

/**
 * The type of the captured error
 */
@property (nonatomic) BSGErrorType type;

@end
