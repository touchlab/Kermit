//
//  BSGGlobals.m
//  Bugsnag
//
//  Created by Nick Dowell on 18/06/2021.
//  Copyright © 2021 Bugsnag Inc. All rights reserved.
//

#import "BSGGlobals.h"

static dispatch_queue_t bsg_g_fileSystemQueue;

static void BSGGlobalsInit(void) {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        bsg_g_fileSystemQueue = dispatch_queue_create("com.bugsnag.filesystem", DISPATCH_QUEUE_SERIAL);
    });
}

dispatch_queue_t BSGGlobalsFileSystemQueue(void) {
    BSGGlobalsInit();
    return bsg_g_fileSystemQueue;
}
