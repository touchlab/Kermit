//
//  BSG_KSMachHeaders.c
//  Bugsnag
//
//  Created by Robin Macharg on 04/05/2020.
//  Copyright © 2020 Bugsnag. All rights reserved.
//

#include "BSG_KSMachHeaders.h"

#include "BSG_KSDynamicLinker.h"
#include "BSG_KSLogger.h"
#include "BSG_KSMach.h"

#include <dispatch/dispatch.h>
#include <dlfcn.h>
#include <mach-o/dyld.h>
#include <stdlib.h>

// Copied from https://github.com/apple/swift/blob/swift-5.0-RELEASE/include/swift/Runtime/Debug.h#L28-L40

#define CRASHREPORTER_ANNOTATIONS_VERSION 5
#define CRASHREPORTER_ANNOTATIONS_SECTION "__crash_info"

struct crashreporter_annotations_t {
    uint64_t version;          // unsigned long
    uint64_t message;          // char *
    uint64_t signature_string; // char *
    uint64_t backtrace;        // char *
    uint64_t message2;         // char *
    uint64_t thread;           // uint64_t
    uint64_t dialog_mode;      // unsigned int
    uint64_t abort_cause;      // unsigned int
};

// MARK: - Mach Header Linked List

static BSG_Mach_Header_Info *bsg_g_mach_headers_images_head;
static BSG_Mach_Header_Info *bsg_g_mach_headers_images_tail;
static dispatch_queue_t bsg_g_serial_queue;

BSG_Mach_Header_Info *bsg_mach_headers_get_images() {
    if (!bsg_g_mach_headers_images_head) {
        bsg_mach_headers_initialize();
        bsg_mach_headers_register_for_changes();
    }
    return bsg_g_mach_headers_images_head;
}

BSG_Mach_Header_Info *bsg_mach_headers_get_main_image() {
    BSG_Mach_Header_Info *img = bsg_mach_headers_get_images();
    while (img && !img->isMain) {
        img = img->next;
    }
    return img;
}

void bsg_mach_headers_initialize() {
    
    // Clear any existing headers to reset the head/tail pointers
    for (BSG_Mach_Header_Info *img = bsg_g_mach_headers_images_head; img != NULL; ) {
        BSG_Mach_Header_Info *imgToDelete = img;
        img = img->next;
        free(imgToDelete);
    }
    
    bsg_g_mach_headers_images_head = NULL;
    bsg_g_mach_headers_images_tail = NULL;
    bsg_g_serial_queue = dispatch_queue_create("com.bugsnag.mach-headers", DISPATCH_QUEUE_SERIAL);
}

void bsg_mach_headers_register_for_changes() {
    
    // Register for binary images being loaded and unloaded. dyld calls the add function once
    // for each library that has already been loaded and then keeps this cache up-to-date
    // with future changes
    _dyld_register_func_for_add_image(&bsg_mach_headers_add_image);
    _dyld_register_func_for_remove_image(&bsg_mach_headers_remove_image);

}

/**
 * Populate a Mach binary image info structure
 *
 * @param header The Mach binary image header
 *
 * @param info Encapsulated Binary Image info
 *
 * @returns a boolean indicating success
 */
bool bsg_mach_headers_populate_info(const struct mach_header *header, intptr_t slide, BSG_Mach_Header_Info *info) {
    
    // Early exit conditions; this is not a valid/useful binary image
    // 1. We can't find a sensible Mach command
    uintptr_t cmdPtr = bsg_mach_headers_first_cmd_after_header(header);
    if (cmdPtr == 0) {
        return false;
    }

    // 2. The image doesn't have a name.  Note: running with a debugger attached causes this condition to match.
    Dl_info DlInfo = (const Dl_info) { 0 };
    dladdr(header, &DlInfo);
    const char *imageName = DlInfo.dli_fname;
    if (!imageName) {
        return false;
    }
    
    // Look for the TEXT segment to get the image size.
    // Also look for a UUID command.
    uint64_t imageSize = 0;
    uint64_t imageVmAddr = 0;
    uint8_t *uuid = NULL;

    for (uint32_t iCmd = 0; iCmd < header->ncmds; iCmd++) {
        struct load_command *loadCmd = (struct load_command *)cmdPtr;
        switch (loadCmd->cmd) {
        case LC_SEGMENT: {
            struct segment_command *segCmd = (struct segment_command *)cmdPtr;
            if (strcmp(segCmd->segname, SEG_TEXT) == 0) {
                imageSize = segCmd->vmsize;
                imageVmAddr = segCmd->vmaddr;
            }
            break;
        }
        case LC_SEGMENT_64: {
            struct segment_command_64 *segCmd =
                (struct segment_command_64 *)cmdPtr;
            if (strcmp(segCmd->segname, SEG_TEXT) == 0) {
                imageSize = segCmd->vmsize;
                imageVmAddr = segCmd->vmaddr;
            }
            break;
        }
        case LC_UUID: {
            struct uuid_command *uuidCmd = (struct uuid_command *)cmdPtr;
            uuid = uuidCmd->uuid;
            break;
        }
        case LC_MAIN:
        case LC_UNIXTHREAD:
            info->isMain = true;
            break;
        }
        cmdPtr += loadCmd->cmdsize;
    }
    
    // Sanity checks that should never fail
    if (((uintptr_t)imageVmAddr + (uintptr_t)slide) != (uintptr_t)header) {
        BSG_KSLOG_ERROR("Mach header != (vmaddr + slide) for %s; symbolication will be compromised.", imageName);
    }
    if ((uintptr_t)DlInfo.dli_fbase != (uintptr_t)header) {
        BSG_KSLOG_ERROR("Mach header != dli_fbase for %s", imageName);
    }
    
    info->header = header;
    info->imageSize = imageSize;
    info->imageVmAddr = imageVmAddr;
    info->uuid = uuid;
    info->name = imageName;
    info->slide = slide;
    info->unloaded = FALSE;
    info->next = NULL;
    
    return true;
}

void bsg_mach_headers_add_image(const struct mach_header *header, intptr_t slide) {
    BSG_Mach_Header_Info *newImage = calloc(1, sizeof(BSG_Mach_Header_Info));
    if (newImage != NULL) {
        if (bsg_mach_headers_populate_info(header, slide, newImage)) {
            dispatch_sync(bsg_g_serial_queue, ^{
                if (bsg_g_mach_headers_images_head == NULL) {
                    bsg_g_mach_headers_images_head = newImage;
                } else {
                    bsg_g_mach_headers_images_tail->next = newImage;
                }
                bsg_g_mach_headers_images_tail = newImage;
            });
        } else {
            free(newImage);
        }
    }
}

/**
  * To avoid a destructive operation that could lead thread safety problems, we maintain the
  * image record, but mark it as unloaded
 */
void bsg_mach_headers_remove_image(const struct mach_header *header, intptr_t slide) {
    BSG_Mach_Header_Info existingImage = { 0 };
    if (bsg_mach_headers_populate_info(header, slide, &existingImage)) {
        for (BSG_Mach_Header_Info *img = bsg_g_mach_headers_images_head; img != NULL; img = img->next) {
            if (img->imageVmAddr == existingImage.imageVmAddr) {
                img->unloaded = true;
            }
        }
    }
}

BSG_Mach_Header_Info *bsg_mach_headers_image_named(const char *const imageName, bool exactMatch) {
        
    if (imageName != NULL) {
        
        for (BSG_Mach_Header_Info *img = bsg_g_mach_headers_images_head; img != NULL; img = img->next) {
            if (img->name == NULL) {
                continue; // name is null if the index is out of range per dyld(3)
            } else if (img->unloaded == true) {
                continue; // ignore unloaded libraries
            } else if (exactMatch) {
                if (strcmp(img->name, imageName) == 0) {
                    return img;
                }
            } else {
                if (strstr(img->name, imageName) != NULL) {
                    return img;
                }
            }
        }
    }
    
    return NULL;
}

BSG_Mach_Header_Info *bsg_mach_headers_image_at_address(const uintptr_t address) {
    for (BSG_Mach_Header_Info *img = bsg_mach_headers_get_images(); img; img = img->next) {
        if (img->unloaded == true) {
            continue;
        }
        uintptr_t imageAddress = (uintptr_t)img->header;
        if (address >= imageAddress &&
            address < (imageAddress + img->imageSize)) {
            return img;
        }
    }
    return NULL;
}

uintptr_t bsg_mach_headers_first_cmd_after_header(const struct mach_header *const header) {
    if (header == NULL) {
      return 0;
    }
    switch (header->magic) {
    case MH_MAGIC:
    case MH_CIGAM:
        return (uintptr_t)(header + 1);
    case MH_MAGIC_64:
    case MH_CIGAM_64:
        return (uintptr_t)(((const struct mach_header_64 *)header) + 1);
    default:
        // Header is corrupt
        return 0;
    }
}

uintptr_t bsg_mach_headers_image_at_base_of_image_index(const struct mach_header *const header) {
    // Look for a segment command and return the file image address.
    uintptr_t cmdPtr = bsg_mach_headers_first_cmd_after_header(header);
    if (cmdPtr == 0) {
        return 0;
    }
    for (uint32_t i = 0; i < header->ncmds; i++) {
        const struct load_command *loadCmd = (struct load_command *)cmdPtr;
        if (loadCmd->cmd == LC_SEGMENT) {
            const struct segment_command *segmentCmd =
                (struct segment_command *)cmdPtr;
            if (strcmp(segmentCmd->segname, SEG_LINKEDIT) == 0) {
                return segmentCmd->vmaddr - segmentCmd->fileoff;
            }
        } else if (loadCmd->cmd == LC_SEGMENT_64) {
            const struct segment_command_64 *segmentCmd =
                (struct segment_command_64 *)cmdPtr;
            if (strcmp(segmentCmd->segname, SEG_LINKEDIT) == 0) {
                return (uintptr_t)(segmentCmd->vmaddr - segmentCmd->fileoff);
            }
        }
        cmdPtr += loadCmd->cmdsize;
    }

    return 0;
}
static uintptr_t bsg_mach_header_info_get_section_addr_named(const BSG_Mach_Header_Info *header, const char *name) {
    uintptr_t cmdPtr = bsg_mach_headers_first_cmd_after_header(header->header);
    if (!cmdPtr) {
        return 0;
    }
    for (uint32_t i = 0; i < header->header->ncmds; i++) {
        const struct load_command *loadCmd = (struct load_command *)cmdPtr;
        if (loadCmd->cmd == LC_SEGMENT) {
            const struct segment_command *segment = (void *)cmdPtr;
            char *sectionPtr = (void *)(cmdPtr + sizeof(*segment));
            for (uint32_t j = 0; j < segment->nsects; j++) {
                struct section *section = (void *)sectionPtr;
                if (strcmp(name, section->sectname) == 0) {
                    return section->addr + (uintptr_t)header->slide;
                }
                sectionPtr += sizeof(*section);
            }
        } else if (loadCmd->cmd == LC_SEGMENT_64) {
            const struct segment_command_64 *segment = (void *)cmdPtr;
            char *sectionPtr = (void *)(cmdPtr + sizeof(*segment));
            for (uint32_t j = 0; j < segment->nsects; j++) {
                struct section_64 *section = (void *)sectionPtr;
                if (strcmp(name, section->sectname) == 0) {
                    return (uintptr_t)section->addr + (uintptr_t)header->slide;
                }
                sectionPtr += sizeof(*section);
            }
        }
        cmdPtr += loadCmd->cmdsize;
    }
    return 0;
}

const char *bsg_mach_headers_get_crash_info_message(const BSG_Mach_Header_Info *header) {
    struct crashreporter_annotations_t info;
    uintptr_t sectionAddress = bsg_mach_header_info_get_section_addr_named(header, CRASHREPORTER_ANNOTATIONS_SECTION);
    if (!sectionAddress) {
        return NULL;
    }
    if (bsg_ksmachcopyMem((void *)sectionAddress, &info, sizeof(info)) != KERN_SUCCESS) {
        return NULL;
    }
    // Version 4 was in use until iOS 9 / Swift 2.0 when the version was bumped to 5.
    if (info.version > CRASHREPORTER_ANNOTATIONS_VERSION) {
        return NULL;
    }
    if (!info.message) {
        return NULL;
    }
    // Probe the string to ensure it's safe to read.
    for (uintptr_t i = 0; i < 500; i++) {
        char c;
        if (bsg_ksmachcopyMem((void *)(info.message + i), &c, sizeof(c)) != KERN_SUCCESS) {
            // String is not readable.
            return NULL;
        }
        if (c == '\0') {
            // Found end of string.
            return (const char *)info.message;
        }
    }
    return NULL;
}
