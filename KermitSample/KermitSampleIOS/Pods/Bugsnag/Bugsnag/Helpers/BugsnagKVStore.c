//
//  BugsnagKVStore.c
//  Bugsnag
//
//  Created by Karl Stenerud on 11.09.20.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//

#include "BugsnagKVStore.h"

#include <dirent.h>
#include <errno.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <limits.h>

static DIR* g_currentDir = NULL;
static int g_currentDirFD = 0;
static char g_path[PATH_MAX+1];

static int openKeyRead(const char* name) {
    return openat(g_currentDirFD, name, O_RDONLY, 0);
}

static int openKeyWrite(const char* name) {
    return openat(g_currentDirFD, name, O_CREAT | O_RDWR | O_TRUNC, 0600);
}

void bsgkv_open(const char* path, int* err) {
    bsgkv_close();

    g_currentDir = opendir(path);
    if(g_currentDir == NULL) {
        if(errno == ENOENT) {
            if(mkdir(path, 0700) != 0) {
                *err = errno;
                return;
            }
            g_currentDir = opendir(path);
            if(g_currentDir == NULL) {
                *err = errno;
                return;
            }
        } else {
            *err = errno;
            return;
        }
    }

    g_currentDirFD = dirfd(g_currentDir);
    if(g_currentDirFD < 0) {
        closedir(g_currentDir);
        g_currentDir = NULL;
        *err = errno;
        return;
    }

    strncpy(g_path, path, sizeof(g_path));
    g_path[sizeof(g_path)-1] = 0;
    *err = 0;
}

void bsgkv_close(void) {
    if(g_currentDir != NULL) {
        closedir(g_currentDir);
        g_currentDir = NULL;
        // Note: closedir() closes the underlying file descriptor.
        // Attempting to close it again is a programming error.
        g_currentDirFD = 0;
    }
}

void bsgkv_purge(int* err) {
    // Set up a baseline path buffer to append the file names onto.
    char path[sizeof(g_path)];
    strcpy(path, g_path);
    size_t basePathLength = strlen(path);
    if(basePathLength >= PATH_MAX-2) {
        *err = E2BIG;
        return;
    }
    path[basePathLength++] = '/';
    path[basePathLength] = 0;
    char* const filename = path + basePathLength;
    const size_t nameMaxLength = PATH_MAX - basePathLength;

    *err = 0;

    // Step through K-V store directory, deleting all regular files.
    rewinddir(g_currentDir);
    for(;;) {
        errno = 0;
        struct dirent* dent = readdir(g_currentDir);
        if(dent == NULL) {
            if(errno != 0) {
                *err = errno;
                return;
            }
            break;
        }
        if(dent->d_type != DT_REG) {
            continue;
        }
        if(dent->d_namlen > nameMaxLength) {
            // Set an error but don't stop purging
            *err = E2BIG;
        } else {
            memcpy(filename, dent->d_name, dent->d_namlen);
            filename[dent->d_namlen] = 0;
            if(unlink(path) != 0) {
                // Set an error but don't stop purging
                *err = errno;
            }
        }
    }

    rewinddir(g_currentDir);
}

void bsgkv_delete(const char* key, int* err) {
    if(unlinkat(g_currentDirFD, key, 0) < 0) {
        if(errno != ENOENT) {
            *err = errno;
            return;
        }
    }
    *err = 0;
}

void bsgkv_setBytes(const char* key, const uint8_t* value, int length, int* err) {
    int fd = openKeyWrite(key);
    if(fd < 0) {
        *err = errno;
        goto cleanup_fail;
    }

retry_after_eintr:
    if (write(fd, value, (size_t)length) == length) {
        goto cleanup_success;
    }

    if(errno == EINTR) {
        if(lseek(fd, 0, SEEK_SET) < 0)
        {
            *err = errno;
            goto cleanup_fail;
        }
        goto retry_after_eintr;
    }

    *err = errno;
    if(*err == 0) {
        // Disk full, or OS resource limit reached. Just call it IO error since these are
        // catastrophic, we can't do anything about it, and the app WILL die very soon.
        *err = EIO;
    }
    goto cleanup_fail;

cleanup_success:
    *err = 0;
cleanup_fail:
    if(fd > 0) {
        close(fd);
    }
}

void bsgkv_getBytes(const char* key, uint8_t* value, int* length, int* err) {
    int fd = openKeyRead(key);
    if(fd < 0) {
        *err = errno;
        return;
    }
    size_t bytesRequested = (size_t)*length;
    ssize_t bytesRead = read(fd, value, bytesRequested);
    if(bytesRead < 0) {
        *err = errno;
        return;
    }
    close(fd);
    *length = (int)bytesRead;
    *err = 0;
}

void bsgkv_setString(const char* key, const char* value, int* err) {
    bsgkv_setBytes(key, (const uint8_t*)value, (int)strlen(value), err);
}

void bsgkv_getString(const char* key, char* value, int maxLength, int* err) {
    int length = maxLength;
    bsgkv_getBytes(key, (uint8_t*)value, &length, err);
    if(*err != 0) {
        return;
    }

    if(length >= maxLength) {
        value[maxLength-1] = 0;
    } else {
        value[length] = 0;
    }
}

#define DECLARE_SCALAR_GETTER_SETTER(NAME, TYPE) \
void bsgkv_set##NAME(const char* key, TYPE value, int* err) { \
    bsgkv_setBytes(key, (uint8_t*)&value, sizeof(value), err); \
} \
TYPE bsgkv_get##NAME(const char* key, int* err) { \
    TYPE value = 0; \
    int length = sizeof(value); \
    bsgkv_getBytes(key, (uint8_t*)&value, &length, err); \
    return value; \
}

DECLARE_SCALAR_GETTER_SETTER(Int, int64_t)
DECLARE_SCALAR_GETTER_SETTER(Float, double)
DECLARE_SCALAR_GETTER_SETTER(Boolean, bool)
