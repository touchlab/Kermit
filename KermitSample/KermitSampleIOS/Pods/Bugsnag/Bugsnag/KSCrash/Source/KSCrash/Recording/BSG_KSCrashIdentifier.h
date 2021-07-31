#ifndef HDR_BSG_KSCrashIdentifier_h
#define HDR_BSG_KSCrashIdentifier_h
#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

void bsg_kscrash_generate_report_initialize(const char *directory);

/**
 * Generates a new UUID. Not async signal safe. Caller responsible for
 * freeing allocated string.
 */
char *bsg_kscrash_generate_report_identifier(void);
/**
 * Generates a new path string. Not async signal safe. Caller responsible
 * for freeing allocated string.
 */
char *bsg_kscrash_generate_report_path(const char *identifier, bool is_recrash_report);

#ifdef __cplusplus
}
#endif

#endif // HDR_BSG_KSCrashIdentifier_h
