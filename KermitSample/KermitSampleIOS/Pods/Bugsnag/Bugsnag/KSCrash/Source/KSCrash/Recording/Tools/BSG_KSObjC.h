//
//  BSG_KSObjC.h
//
//  Created by Karl Stenerud on 2012-08-30.
//
//  Copyright (c) 2012 Karl Stenerud. All rights reserved.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall remain in place
// in this source code.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

#ifndef HDR_BSG_KSObjC_h
#define HDR_BSG_KSObjC_h

#ifdef __cplusplus
extern "C" {
#endif

#include <CoreFoundation/CoreFoundation.h>
#include <mach/kern_return.h>

typedef CF_ENUM(unsigned, BSG_KSObjCType) {
    BSG_KSObjCTypeUnknown = 0,
    BSG_KSObjCTypeClass,
    BSG_KSObjCTypeObject,
    BSG_KSObjCTypeBlock,
};

typedef CF_ENUM(unsigned, BSG_KSObjCClassType) {
    BSG_KSObjCClassTypeUnknown = 0,
    BSG_KSObjCClassTypeString,
    BSG_KSObjCClassTypeDate,
    BSG_KSObjCClassTypeURL,
    BSG_KSObjCClassTypeArray,
    BSG_KSObjCClassTypeDictionary,
    BSG_KSObjCClassTypeNumber,
    BSG_KSObjCClassTypeException,
};

//======================================================================
#pragma mark - Initialization -
//======================================================================

/** Initialize BSG_KSObjC.
 */
void bsg_ksobjc_init(void);

//======================================================================
#pragma mark - Basic Objective-C Queries -
//======================================================================

/** Check if a pointer is a tagged pointer or not.
 *
 * @param pointer The pointer to check.
 * @return true if it's a tagged pointer.
 */
bool bsg_ksobjc_bsg_isTaggedPointer(const void *const pointer);

#ifdef __cplusplus
}
#endif

#endif // HDR_BSG_KSObjC_h
