//
//  BugsnagStackframe.h
//  Bugsnag
//
//  Created by Jamie Lynch on 01/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NSString * BugsnagStackframeType NS_TYPED_ENUM;

FOUNDATION_EXPORT BugsnagStackframeType const BugsnagStackframeTypeCocoa;

/**
 * Represents a single stackframe from a stacktrace.
 */
@interface BugsnagStackframe : NSObject

/**
 * The method name of the stackframe
 */
@property (copy, nullable, nonatomic) NSString *method;

/**
 * The Mach-O file used by the stackframe
 */
@property (copy, nullable, nonatomic) NSString *machoFile;

/**
 * A UUID identifying the Mach-O file used by the stackframe
 */
@property (copy, nullable, nonatomic) NSString *machoUuid;

/**
 * The stack frame address
 */
@property (strong, nullable, nonatomic) NSNumber *frameAddress;

/**
 * The VM address of the Mach-O file
 */
@property (strong, nullable, nonatomic) NSNumber *machoVmAddress;

/**
 * The address of the stackframe symbol
 */
@property (strong, nullable, nonatomic) NSNumber *symbolAddress;

/**
 * The load address of the Mach-O file
 */
@property (strong, nullable, nonatomic) NSNumber *machoLoadAddress;

/**
 * Whether the frame was within the program counter
 */
@property (nonatomic) BOOL isPc;

/**
 * Whether the frame was within the link register
 */
@property (nonatomic) BOOL isLr;

/**
 * The type of the stack frame, if it differs from that of the containing error or event.
 */
@property (copy, nullable, nonatomic) BugsnagStackframeType type;

/**
 * Returns an array of stackframe objects representing the provided call stack strings.
 *
 * The call stack strings should follow the format used by `[NSThread callStackSymbols]` and `backtrace_symbols()`.
 */
+ (nullable NSArray<BugsnagStackframe *> *)stackframesWithCallStackSymbols:(NSArray<NSString *> *)callStackSymbols;

@end

NS_ASSUME_NONNULL_END
