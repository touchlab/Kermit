//
// Created by Kevin Galligan on 10/11/21.
//

#import "crashlytics.h"


@implementation FIRCrashlytics
+ (instancetype)crashlytics {
    return [[FIRCrashlytics alloc] init];
}

- (void)log:(NSString *)msg {

}

- (void)setCustomValue:(id)value forKey:(NSString *)key {

}

- (void)recordExceptionModel:(FIRExceptionModel *)exceptionModel {

}

@end