// Copyright 2019 Google
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

#import <Foundation/Foundation.h>

#import "FIRExceptionModel.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * The Firebase Crashlytics API provides methods to annotate and manage fatal and
 * non-fatal reports captured and reported to Firebase Crashlytics.
 *
 * By default, Firebase Crashlytics is initialized with `[FIRApp configure]`.
 *
 * Note: The Crashlytics class cannot be subclassed. If this makes testing difficult,
 * we suggest using a wrapper class or a protocol extension.
 */
NS_SWIFT_NAME(Crashlytics)
@interface FIRCrashlytics : NSObject

/**
 * Accesses the singleton Crashlytics instance.
 *
 * @return The singleton Crashlytics instance.
 */
+ (instancetype)crashlytics NS_SWIFT_NAME(crashlytics());

/**
 * Adds logging that is sent with your crash data. The logging does not appear  in the
 * system.log and is only visible in the Crashlytics dashboard.
 *
 * @param msg Message to log
 */
- (void)log:(NSString *)msg;

/**
 * Records an Exception Model described by an FIRExceptionModel object. The events are
 * grouped and displayed similarly to crashes. Keep in mind that this method can be expensive.
 * The total number of FIRExceptionModels that can be recorded during your app's life-cycle is
 * limited by a fixed-size circular buffer. If the buffer is overrun, the oldest data is dropped.
 * Exception Models are relayed to Crashlytics on a subsequent launch of your application.
 *
 * @param exceptionModel Instance of the FIRExceptionModel to be recorded
 */
- (void)recordExceptionModel:(FIRExceptionModel *)exceptionModel
    NS_SWIFT_NAME(record(exceptionModel:));

@end

NS_ASSUME_NONNULL_END
