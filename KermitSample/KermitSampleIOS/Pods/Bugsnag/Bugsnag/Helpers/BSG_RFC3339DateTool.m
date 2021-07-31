//
// RFC3339DateTool.m
//
// Copyright 2010 Karl Stenerud
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

#import "BSG_RFC3339DateTool.h"

// New formatter: Everything is UTC with milliseconds
static NSDateFormatter *g_currentDateFormatter;

// Old formatter: Everything is UTC, no milliseconds
static NSDateFormatter *g_utcDateFormatter;

// Oldx2 formatter: Time zones can be specified
static NSDateFormatter *g_timezoneAllowedDateFormatter;

@implementation BSG_RFC3339DateTool

+ (void)initialize {
    NSLocale *locale = [[NSLocale alloc] initWithLocaleIdentifier:@"en_US_POSIX"];
    NSTimeZone *zone = [NSTimeZone timeZoneForSecondsFromGMT:0];

    g_currentDateFormatter = [NSDateFormatter new];
    [g_currentDateFormatter setLocale:locale];
    [g_currentDateFormatter setTimeZone:zone];
    [g_currentDateFormatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'"];

    g_utcDateFormatter = [NSDateFormatter new];
    [g_utcDateFormatter setLocale:locale];
    [g_utcDateFormatter setTimeZone:zone];
    [g_utcDateFormatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'"];

    g_timezoneAllowedDateFormatter = [NSDateFormatter new];
    [g_timezoneAllowedDateFormatter setLocale:locale];
    [g_timezoneAllowedDateFormatter setTimeZone:zone];
    [g_timezoneAllowedDateFormatter setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ssZZZ"];
}


+ (NSString *)stringFromDate:(NSDate *)date {
    if (![date isKindOfClass:[NSDate class]]) {
        return nil;
    }
    return [g_currentDateFormatter stringFromDate:date];
}

+ (NSDate *)dateFromString:(NSString *)string {
    if (![string isKindOfClass:[NSString class]]) {
        return nil;
    }
    NSDate *date = nil;

    if((date = [g_currentDateFormatter dateFromString:string]) != nil) {
        return date;
    }

    // Fallback to older date formats
    if((date = [g_utcDateFormatter dateFromString:string]) != nil) {
        return date;
    }
    
    return [g_timezoneAllowedDateFormatter dateFromString:string];
}

+ (NSString *)stringFromUNIXTimestamp:(NSTimeInterval)timestamp {
    return
        [self stringFromDate:[NSDate dateWithTimeIntervalSince1970:timestamp]];
}

+ (BOOL)isLikelyDateString:(NSString *)string {
    const char *ptr = string.UTF8String;
    return (string.length >= 19 &&
            isdigit(ptr[0]) &&
            isdigit(ptr[1]) &&
            isdigit(ptr[2]) &&
            isdigit(ptr[3]) &&
            '-' == (ptr[4]) &&
            isdigit(ptr[5]) &&
            isdigit(ptr[6]) &&
            '-' == (ptr[7]) &&
            isdigit(ptr[8]) &&
            isdigit(ptr[9]) &&
            'T' ==  ptr[10] &&
            isdigit(ptr[11]) &&
            isdigit(ptr[12]) &&
            ':' == (ptr[13]) &&
            isdigit(ptr[14]) &&
            isdigit(ptr[15]) &&
            ':' == (ptr[16]) &&
            isdigit(ptr[17]) &&
            isdigit(ptr[18]));
}

@end
