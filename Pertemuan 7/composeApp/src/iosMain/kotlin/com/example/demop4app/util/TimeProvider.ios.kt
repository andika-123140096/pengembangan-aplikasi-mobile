package com.example.demop4app.util

import platform.Foundation.NSDate

actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000.0).toLong()
}
