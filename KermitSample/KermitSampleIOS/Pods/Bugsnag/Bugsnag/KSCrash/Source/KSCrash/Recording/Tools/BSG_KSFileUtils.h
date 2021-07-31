//
//  KSFileUtils.h
//
//  Created by Karl Stenerud on 2012-01-28.
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

/* Basic file reading/writing functions.
 */

#ifndef HDR_BSG_KSFileUtils_h
#define HDR_BSG_KSFileUtils_h

#ifdef __cplusplus
extern "C" {
#endif

#include <stdbool.h>
#include <sys/types.h>

/** Get the last entry in a file path. Assumes UNIX style separators.
 *
 * @param path The file path.
 *
 * @return the last entry in the path.
 */
const char *bsg_ksfulastPathEntry(const char *path);

/** Write bytes to a file descriptor.
 *
 * @param fd The file descriptor
 *
 * @param bytes Buffer containing the bytes.
 *
 * @param length The number of bytes to write.
 *
 * @return true if the operation was successful.
 */
bool bsg_ksfuwriteBytesToFD(const int fd, const char *bytes, ssize_t length);

/**
 * Flushes the write buffer
 *
 * @param fd The file descriptor
 */
bool bsg_ksfuflushWriteBuffer(const int fd);

#ifdef __cplusplus
}
#endif

#endif // HDR_KSFileUtils_h
