//
//  BugsnagStackframe+Private.h
//  Bugsnag
//
//  Created by Nick Dowell on 20/11/2020.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#import <Bugsnag/BugsnagStackframe.h>

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagStackframe ()

+ (NSArray<BugsnagStackframe *> *)stackframesWithBacktrace:(uintptr_t *)backtrace length:(NSUInteger)length;

+ (NSArray<BugsnagStackframe *> *)stackframesWithCallStackReturnAddresses:(NSArray<NSNumber *> *)callStackReturnAddresses;

/// Constructs a stackframe object from a stackframe dictionary and list of images captured by KSCrash.
+ (nullable instancetype)frameFromDict:(NSDictionary<NSString *, id> *)dict withImages:(NSArray<NSDictionary<NSString *, id> *> *)binaryImages;

/// Constructs a stackframe object from a JSON object (typically loaded from disk.)
+ (instancetype)frameFromJson:(NSDictionary<NSString *, id> *)json;

/// Populates the method and symbolAddress via `dladdr()` if this object was created from a backtrace or callstack.
/// This can be a slow operation, so should be performed on a background thread.
- (void)symbolicateIfNeeded;

/// Returns a JSON compatible representation of the stackframe.
- (NSDictionary *)toDictionary;

@property (nonatomic) BOOL needsSymbolication;

// MARK: - Properties not used for Cocoa stack frames, but used by React Native and Unity.

@property (strong, nullable, nonatomic) NSNumber *columnNumber;
@property (copy, nullable, nonatomic) NSString *file;
@property (strong, nullable, nonatomic) NSNumber *inProject;
@property (strong, nullable, nonatomic) NSNumber *lineNumber;

@end

NS_ASSUME_NONNULL_END
