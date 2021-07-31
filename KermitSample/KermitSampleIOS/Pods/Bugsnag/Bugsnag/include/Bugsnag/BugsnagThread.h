//
//  BugsnagThread.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef NS_OPTIONS(NSUInteger, BSGThreadType) {
    BSGThreadTypeCocoa NS_SWIFT_NAME(cocoa) = 0,
    BSGThreadTypeReactNativeJs = 1 << 1
};

@class BugsnagStackframe;

/**
 * A representation of thread information recorded as part of a BugsnagEvent.
 */
@interface BugsnagThread : NSObject

/**
 * A unique ID which identifies this thread
 */
@property (copy, nullable, nonatomic) NSString *id;

/**
 * The name which identifies this thread
 */
@property (copy, nullable, nonatomic) NSString *name;

/**
 * Whether the error being reported happened in this thread
 */
@property (readonly, nonatomic) BOOL errorReportingThread;

/**
 * Sets a representation of this thread's stacktrace
 */
@property (copy, nonnull, nonatomic) NSArray<BugsnagStackframe *> *stacktrace;

/**
 * Determines the type of thread based on the originating platform
 * (intended for internal use only)
 */
@property (nonatomic) BSGThreadType type;

@end
