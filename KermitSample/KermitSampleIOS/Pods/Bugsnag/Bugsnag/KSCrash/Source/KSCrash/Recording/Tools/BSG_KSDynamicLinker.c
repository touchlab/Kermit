//
//  KSDynamicLinker.c
//
//  Created by Karl Stenerud on 2013-10-02.
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

#include "BSG_KSDynamicLinker.h"
#include "BSG_KSArchSpecific.h"
#include "BSG_KSMachHeaders.h"

#include <limits.h>
#include <mach-o/nlist.h>
#include <string.h>

bool bsg_ksdldladdr(const uintptr_t address, Dl_info *const info) {
    info->dli_fname = NULL;
    info->dli_fbase = NULL;
    info->dli_sname = NULL;
    info->dli_saddr = NULL;

    BSG_Mach_Header_Info *image = bsg_mach_headers_image_at_address(address);
    if (image == NULL) {
        return false;
    }
    if (address < (uintptr_t)image->slide) {
        return false;
    }
    const uintptr_t addressWithSlide = address - (uintptr_t)image->slide;
    const uintptr_t segmentBase =
        bsg_mach_headers_image_at_base_of_image_index(image->header) +
        (uintptr_t)image->slide;
    if (segmentBase == 0) {
        return false;
    }

    info->dli_fname = image->name;
    // Cast away constness because dli_fbase is not :'(
    info->dli_fbase = (void *)(uintptr_t)image->header;

    // Find symbol tables and get whichever symbol is closest to the address.
    const BSG_STRUCT_NLIST *bestMatch = NULL;
    uintptr_t bestDistance = ULONG_MAX;
    uintptr_t cmdPtr = bsg_mach_headers_first_cmd_after_header(image->header);
    if (cmdPtr == 0) {
        return false;
    }
    for (uint32_t iCmd = 0; iCmd < image->header->ncmds; iCmd++) {
        const struct load_command *loadCmd = (struct load_command *)cmdPtr;
        if (loadCmd->cmd == LC_SYMTAB) {
            const struct symtab_command *symtabCmd =
                (struct symtab_command *)cmdPtr;
            const BSG_STRUCT_NLIST *symbolTable =
                (BSG_STRUCT_NLIST *)(segmentBase + symtabCmd->symoff);
            const uintptr_t stringTable = segmentBase + symtabCmd->stroff;

            for (uint32_t iSym = 0; iSym < symtabCmd->nsyms; iSym++) {
                // If n_value is 0, the symbol refers to an external object.
                if (symbolTable[iSym].n_value != 0) {
                    uintptr_t symbolBase = symbolTable[iSym].n_value;
                    uintptr_t currentDistance = addressWithSlide - symbolBase;
                    if ((addressWithSlide >= symbolBase) &&
                        (currentDistance <= bestDistance)) {
                        bestMatch = symbolTable + iSym;
                        bestDistance = currentDistance;
                    }
                }
            }
            if (bestMatch != NULL) {
                info->dli_saddr =
                    (void *)(bestMatch->n_value + (uintptr_t)image->slide);
                info->dli_sname = (char *)((intptr_t)stringTable +
                                           (intptr_t)bestMatch->n_un.n_strx);
                if (*info->dli_sname == '_') {
                    info->dli_sname++;
                }
                // This happens if all symbols have been stripped.
                if (info->dli_saddr == info->dli_fbase &&
                    bestMatch->n_type == 3) {
                    info->dli_sname = NULL;
                }
                break;
            }
        }
        cmdPtr += loadCmd->cmdsize;
    }

    return true;
}

