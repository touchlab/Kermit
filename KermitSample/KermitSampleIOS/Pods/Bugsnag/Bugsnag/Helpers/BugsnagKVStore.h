//
//  BugsnagKVStore.h
//  Bugsnag
//
//  Created by Karl Stenerud on 11.09.20.
//  Copyright © 2020 Bugsnag Inc. All rights reserved.
//
// A low-level filesystem based key-value store. The goal is to keep
// as much processing as possible in syscalls to minimize opportunities
// for the operation to be aborted due to a crash or app shutdown.
//
// Values are stored individually in files (mode 600), with the keys
// used as the file names (so beware of using problematic characters).
// https://en.wikipedia.org/wiki/Filename#Comparison_of_filename_limitations
//
// Warning: Some operating systems (Mac, Windows) have case-insensitive
//          filesystems, which will cause problems if you have two keys
//          with only the letter case different!
//
// Safety:
// - Thread-safe: NO
// - Async-safe: YES
// - Idempotent: YES

#ifndef BugsnagKVStore_h
#define BugsnagKVStore_h

#include <stdbool.h>
#include <stdint.h>

/**
 * Open the key-value store. If the directory doesn't exist, it will be created with mode 700.
 * If the store was already open, it will be closed and reopened at the specified path.
 *
 * Note: This must be called before any other API.
 *
 * File names will be the keys themselves, so it's best to put the kv-store in its own directory.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_open(const char* restrict path, int* restrict err);

/**
 * Close the key-value store.
 */
void bsgkv_close(void);

/**
 * Purge the KV store, deleting all values.
 */
void bsgkv_purge(int* err);

/**
 * Delete the value associated with the specified key.
 * Deleting a non-existent key has no effect, and returns success.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_delete(const char* restrict key, int* restrict err);

/**
 * Store a boolean value, associated with the specified key.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_setBoolean(const char* restrict key, bool value, int* restrict err);

/**
 * Get the boolean value associated with the specified key.
 * If the actual stored value is not a boolean, the result is undefined.
 *
 * err: 0 on success, ENOENT if not found, and errno value on filesystem errors (see errno.h).
 */
bool bsgkv_getBoolean(const char* restrict key, int* restrict err);

/**
 * Store an integer value, associated with the specified key.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_setInt(const char* restrict key, int64_t value, int* restrict err);

/**
 * Get the integer value associated with the specified key.
 * If the actual stored value is not an integer, the result is undefined.
 *
 * err: 0 on success, ENOENT if not found, and errno value on filesystem errors (see errno.h).
 */
int64_t bsgkv_getInt(const char* restrict key, int* restrict err);

/**
 * Store a float value, associated with the specified key.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_setFloat(const char* restrict key, double value, int* restrict err);

/**
 * Get the float value associated with the specified key.
 * If the actual stored value is not a float, the result is undefined.
 *
 * err: 0 on success, ENOENT if not found, and errno value on filesystem errors (see errno.h).
 */
double bsgkv_getFloat(const char* restrict key, int* restrict err);

/**
 * Store a null-terminated string value, associated with the specified key.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_setString(const char* restrict key, const char* restrict value, int* restrict err);

/**
 * Get the null-terminated string value associated with the specified key.
 * If the actual stored value is not a string, the result is undefined.
 * If the key contains >= [maxLength] bytes of data, it will be truncated to [maxLength-1] and null-terminated.
 *
 * err: 0 on success, ENOENT if not found, and errno value on filesystem errors (see errno.h).
 */
void bsgkv_getString(const char* restrict key, char* restrict value, int maxLength, int* restrict err);

/**
 * Store a byte array value, associated with the specified key.
 *
 * err: 0 on success, errno value on filesystem errors (see errno.h).
 */
void bsgkv_setBytes(const char* restrict key, const uint8_t* restrict value, int length, int* restrict err);

/**
 * Get the byte array value associated with the specified key.
 * If the key contains more then [length] bytes of data, it will be truncated to [length].
 *
 * [length] must contain the length of the buffer (value) being passed in.
 * On return, [length] will contain the number of bytes actually filled.
 *
 * err: 0 on success, ENOENT if not found, and errno value on filesystem errors (see errno.h).
 */
void bsgkv_getBytes(const char* restrict key, uint8_t* restrict value, int* length, int* err);

#endif /* BugsnagKVStore_h */
