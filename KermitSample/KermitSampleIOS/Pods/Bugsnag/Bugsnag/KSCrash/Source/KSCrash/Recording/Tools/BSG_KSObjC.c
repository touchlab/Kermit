//
//  BSG_KSObjC.c
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

#include "BSG_KSObjC.h"
#include "BSG_KSObjCApple.h"

#include "BSG_KSMach.h"
#include "BSG_KSString.h"

#include <CoreGraphics/CGBase.h>
#include <objc/runtime.h>

//======================================================================
#pragma mark - Utility -
//======================================================================

#if SUPPORT_TAGGED_POINTERS
bool bsg_isTaggedPointer(const void *pointer) {
    return (((uintptr_t)pointer) & TAG_MASK) != 0;
}
uintptr_t bsg_getTaggedSlot(const void *pointer) {
    return (((uintptr_t)pointer) >> TAG_SLOT_SHIFT) & TAG_SLOT_MASK;
}
uintptr_t bsg_getTaggedPayload(const void *pointer) {
    return (((uintptr_t)pointer) << TAG_PAYLOAD_LSHIFT) >> TAG_PAYLOAD_RSHIFT;
}
#else
bool bsg_isTaggedPointer(__unused const void *pointer) { return false; }
uintptr_t bsg_getTaggedSlot(__unused const void *pointer) { return 0; }
uintptr_t bsg_getTaggedPayload(const void *pointer) {
    return (uintptr_t)pointer;
}
#endif

//======================================================================
#pragma mark - General Queries -
//======================================================================

bool bsg_ksobjc_bsg_isTaggedPointer(const void *const pointer) {
    return bsg_isTaggedPointer(pointer);
}

void bsg_ksobjc_init(void) {
#if SUPPORT_TAGGED_POINTERS

#endif
}
