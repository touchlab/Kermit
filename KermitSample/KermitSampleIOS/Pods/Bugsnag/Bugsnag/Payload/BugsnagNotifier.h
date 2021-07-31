//
//  BugsnagNotifier.h
//  Bugsnag
//
//  Created by Jamie Lynch on 29/04/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface BugsnagNotifier : NSObject

@property (copy, nonatomic) NSString *name;
@property (copy, nonatomic) NSString *version;
@property (copy, nonatomic) NSString *url;
@property (nonatomic) NSMutableArray<BugsnagNotifier *> *dependencies;

- (NSDictionary *)toDict;

@end

NS_ASSUME_NONNULL_END
