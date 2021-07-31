//
//  KSFileUtils.c
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

#include "BSG_KSFileUtils.h"

//#define BSG_KSLogger_LocalLevel TRACE
#include "BSG_KSLogger.h"

#include <errno.h>
#include <string.h>
#include <unistd.h>

const char *bsg_ksfulastPathEntry(const char *const path) {
    if (path == NULL) {
        return NULL;
    }

    char *lastFile = strrchr(path, '/');
    return lastFile == NULL ? path : lastFile + 1;
}

bool bsg_ksfuwriteBytesToFD(const int fd, const char *const bytes,
                            ssize_t length) {
    ssize_t bytesRemaining = length;
    ssize_t bytesWritten = 0;
    const char *unwrittenBytes = (const char *)bytes;

    // write(2) attempts to write the entire length but may write less than
    // that, the exact number of bytes are specified in the return value. In
    // the (unlikely) event that the bytes written are less than length, this
    // function retries with the remaining bytes until all bytes are written,
    // handling potential error cases as needed.
    while (bytesRemaining > 0) {
      bytesWritten = write(fd, unwrittenBytes, (size_t)bytesRemaining);
      if (bytesWritten == -1) {
        // Retry as-is if a signal interrupt occurred, as its a recoverable
        // error. Otherwise exit early as the file descriptor cannot be written
        // (due to lack of disk space, invalid fd, invalid file offset etc).
        //
        // write(2): Upon successful completion the number of bytes which were
        // written is returned.  Otherwise, a -1 is returned and the global
        // variable errno is set to indicate the error.
        //
        // ERRORS
        //   The write(), writev(), and pwrite() system calls will fail and
        //   the file pointer will remain unchanged if:
        //
        //   ...
        //   [EINTR] A signal interrupts the write before it could be completed.
        if (errno != EINTR) {
          return false; // Unrecoverable
        }
      } else {
        bytesRemaining -= bytesWritten;
        unwrittenBytes += bytesWritten;
      }
    }

    return true;
}
