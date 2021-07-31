/**
 * Higher-level user-accessible Bugsnag logging configuration.  Controls how
 * verbose the internal Bugsnag logging is.  Not related to logging Events or
 * other errors with the Bugsnag server.
 *
 * Users can configure a custom logging level in their app as follows:
 *
 * When using Cocoapods to install Bugsnag you can add a `post-install` section
 * to the Podfile:
 *
 *     post_install do |rep|
 *         rep.pods_project.targets.each do |target|
 *             if target.name == "Bugsnag"
 *                 target.build_configurations.each do |config|
 *                     config.build_settings['GCC_PREPROCESSOR_DEFINITIONS'] ||= ['$(inherited)', 'BSG_LOG_LEVEL=BSG_LOGLEVEL_INFO']
 *                 end
 *             end
 *         end
 *     end
 *
 * Change the value of `BSG_LOG_LEVEL` to one of the levels given below and run `pod install`.
 *
 * Note: There is also lower-level KSCrash logging configuration in BSG_KSLogger.h
 *       That file includes this one.  No further configuration is required.
 */

#define BSG_LOGLEVEL_NONE 0
#define BSG_LOGLEVEL_ERR 10
#define BSG_LOGLEVEL_WARN 20
#define BSG_LOGLEVEL_INFO 30
#define BSG_LOGLEVEL_DEBUG 40
#define BSG_LOGLEVEL_TRACE 50

#ifndef BSG_LOG_LEVEL
#define BSG_LOG_LEVEL BSG_LOGLEVEL_INFO
#endif

#ifdef __OBJC__

#import <Foundation/Foundation.h>

#if BSG_LOG_LEVEL >= BSG_LOGLEVEL_ERR
#define bsg_log_err(...) NSLog(@"[Bugsnag] [ERROR] " __VA_ARGS__)
#else
#define bsg_log_err(format, ...)
#endif

#if BSG_LOG_LEVEL >= BSG_LOGLEVEL_WARN
#define bsg_log_warn(...) NSLog(@"[Bugsnag] [WARN] " __VA_ARGS__)
#else
#define bsg_log_warn(format, ...)
#endif

#if BSG_LOG_LEVEL >= BSG_LOGLEVEL_INFO
#define bsg_log_info(...) NSLog(@"[Bugsnag] [INFO] " __VA_ARGS__)
#else
#define bsg_log_info(format, ...)
#endif

#if BSG_LOG_LEVEL >= BSG_LOGLEVEL_DEBUG
#define bsg_log_debug(...) NSLog(@"[Bugsnag] [DEBUG] " __VA_ARGS__)
#else
#define bsg_log_debug(format, ...)
#endif

#endif
