//
//  BugsnagErrorTypes.h
//  Bugsnag
//
//  Created by Jamie Lynch on 22/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 * The types of error that should be reported.
 */
@interface BugsnagErrorTypes : NSObject

/**
 * Determines whether App Hang events should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL appHangs;

/**
 * Determines whether Out of Memory events should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL ooms;

/**
 * Determines whether NSExceptions should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL unhandledExceptions;

/**
 * Determines whether signals should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL signals;

/**
 * Determines whether C errors should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL cppExceptions;

/**
 * Determines whether Mach Exceptions should be reported to bugsnag.
 *
 * This flag is true by default.
 */
@property (nonatomic) BOOL machExceptions;

/**
 * Sets whether Bugsnag should automatically capture and report unhandled promise rejections.
 * This only applies to React Native apps.
 * By default, this value is true.
 */
@property (nonatomic) BOOL unhandledRejections;

@end
