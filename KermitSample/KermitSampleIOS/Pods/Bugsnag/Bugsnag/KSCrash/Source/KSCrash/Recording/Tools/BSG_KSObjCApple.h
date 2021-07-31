//
//  BSG_KSObjCApple.h
//
//  Created by Karl Stenerud on 2012-08-30.
//
// Copyright (c) 2011 Apple Inc. All rights reserved.
//
// This file contains Original Code and/or Modifications of Original Code
// as defined in and that are subject to the Apple Public Source License
// Version 2.0 (the 'License'). You may not use this file except in
// compliance with the License. Please obtain a copy of the License at
// http://www.opensource.apple.com/apsl/ and read it before using this
// file.
//

// This file contains structures and constants copied from Apple header
// files, arranged for use in BSG_KSObjC.

#ifndef HDR_BSG_KSObjCApple_h
#define HDR_BSG_KSObjCApple_h

#ifdef __cplusplus
extern "C" {
#endif

#include <objc/objc.h>

#define MAKE_LIST_T(TYPE)                                                      \
    typedef struct TYPE##_list_t {                                             \
        uint32_t entsizeAndFlags;                                              \
        uint32_t count;                                                        \
        TYPE##_t first;                                                        \
    } TYPE##_list_t;                                                           \
    typedef TYPE##_list_t TYPE##_array_t

#define OBJC_OBJECT(NAME)                                                      \
    NAME {                                                                     \
        Class isa OBJC_ISA_AVAILABILITY;

// ======================================================================
#pragma mark - objc4-680/runtime/objc-msg-x86_64.s -
// and objc4-680/runtime/objc-msg-arm64.s
// ======================================================================

#if __x86_64__
#define ISA_TAG_MASK 1UL
#define ISA_MASK 0x00007ffffffffff8UL
#elif defined(__arm64__)
#define ISA_TAG_MASK 1UL
#define ISA_MASK 0x00000001fffffff8UL
#else
#define ISA_TAG_MASK 0UL
#define ISA_MASK ~1UL
#endif

// ======================================================================
#pragma mark - objc4-680/runtime/objc-config.h -
// ======================================================================

// Define SUPPORT_TAGGED_POINTERS=1 to enable tagged pointer objects
// Be sure to edit tagged pointer SPI in objc-internal.h as well.
#if !(__LP64__)
#define SUPPORT_TAGGED_POINTERS 0
#else
#define SUPPORT_TAGGED_POINTERS 1
#endif

// Define SUPPORT_MSB_TAGGED_POINTERS to use the MSB
// as the tagged pointer marker instead of the LSB.
// Be sure to edit tagged pointer SPI in objc-internal.h as well.
#if !SUPPORT_TAGGED_POINTERS || !TARGET_OS_IPHONE
#define SUPPORT_MSB_TAGGED_POINTERS 0
#else
#define SUPPORT_MSB_TAGGED_POINTERS 1
#endif

// ======================================================================
#pragma mark - objc4-680/runtime/objc-object.h -
// ======================================================================

#if SUPPORT_TAGGED_POINTERS

// KS: The original values wouldn't have worked. The slot shift and mask
// were incorrect.
#define TAG_COUNT 8
//#define TAG_SLOT_MASK 0xf
#define TAG_SLOT_MASK 0x07

#if SUPPORT_MSB_TAGGED_POINTERS
#define TAG_MASK (1ULL << 63)
#define TAG_SLOT_SHIFT 60
#define TAG_PAYLOAD_LSHIFT 4
#define TAG_PAYLOAD_RSHIFT 4
#else
#define TAG_MASK 1
//#   define TAG_SLOT_SHIFT 0
#define TAG_SLOT_SHIFT 1
#define TAG_PAYLOAD_LSHIFT 0
#define TAG_PAYLOAD_RSHIFT 4
#endif

#endif

// ======================================================================
#pragma mark - objc4-680/runtime/objc-internal.h -
// ======================================================================

enum {
    OBJC_TAG_NSAtom = 0,
    OBJC_TAG_1 = 1,
    OBJC_TAG_NSString = 2,
    OBJC_TAG_NSNumber = 3,
    OBJC_TAG_NSIndexPath = 4,
    OBJC_TAG_NSManagedObjectID = 5,
    OBJC_TAG_NSDate = 6,
    OBJC_TAG_7 = 7
};

// ======================================================================
#pragma mark - objc4-680/runtime/objc-os.h -
// ======================================================================

#ifdef __LP64__
#define WORD_SHIFT 3UL
#define WORD_MASK 7UL
#define WORD_BITS 64
#else
#define WORD_SHIFT 2UL
#define WORD_MASK 3UL
#define WORD_BITS 32
#endif

// ======================================================================
#pragma mark - objc4-680/runtime/runtime.h -
// ======================================================================

typedef struct objc_cache *Cache;

// ======================================================================
#pragma mark - objc4-680/runtime/objc-runtime-new.h -
// ======================================================================

typedef struct method_t {
    SEL name;
    const char *types;
    IMP imp;
} method_t;

MAKE_LIST_T(method);

typedef struct ivar_t {
#if __x86_64__
// *offset was originally 64-bit on some x86_64 platforms.
// We read and write only 32 bits of it.
// Some metadata provides all 64 bits. This is harmless for unsigned
// little-endian values.
// Some code uses all 64 bits. class_addIvar() over-allocates the
// offset for their benefit.
#endif
    int32_t *offset;
    const char *name;
    const char *type;
    // alignment is sometimes -1; use alignment() instead
    uint32_t alignment_raw;
    uint32_t size;
} ivar_t;

MAKE_LIST_T(ivar);

typedef struct property_t {
    const char *name;
    const char *attributes;
} property_t;

MAKE_LIST_T(property);

typedef struct OBJC_OBJECT(protocol_t) const char *mangledName;
struct protocol_list_t *protocols;
method_list_t *instanceMethods;
method_list_t *classMethods;
method_list_t *optionalInstanceMethods;
method_list_t *optionalClassMethods;
property_list_t *instanceProperties;
uint32_t size; // sizeof(protocol_t)
uint32_t flags;
// Fields below this point are not always present on disk.
const char **extendedMethodTypes;
const char *_demangledName;
}
protocol_t;

MAKE_LIST_T(protocol);

// Values for class_ro_t->flags
// These are emitted by the compiler and are part of the ABI.
// class is a metaclass
#define RO_META (1 << 0)
// class is a root class
#define RO_ROOT (1 << 1)

typedef struct class_ro_t {
    uint32_t flags;
    uint32_t instanceStart;
    uint32_t instanceSize;
#ifdef __LP64__
    uint32_t reserved;
#endif

    const uint8_t *ivarLayout;

    const char *name;
    method_list_t *baseMethodList;
    protocol_list_t *baseProtocols;
    const ivar_list_t *ivars;

    const uint8_t *weakIvarLayout;
    property_list_t *baseProperties;
} class_ro_t;

typedef struct class_rw_t {
    uint32_t flags;
    uint32_t version;

    const class_ro_t *ro;

    method_array_t methods;
    property_array_t properties;
    protocol_array_t protocols;

    Class firstSubclass;
    Class nextSiblingClass;

    char *demangledName;
} class_rw_t;

typedef struct class_t {
    struct class_t *isa;
    struct class_t *superclass;
#pragma clang diagnostic push
#pragma GCC diagnostic ignored "-Wdeprecated-declarations"
    Cache cache;
#pragma clang diagnostic pop
    IMP *vtable;
    uintptr_t data_NEVER_USE; // class_rw_t * plus custom rr/alloc flags
} class_t;

#ifdef __cplusplus
}
#endif

#endif // HDR_BSG_KSObjCApple_h
