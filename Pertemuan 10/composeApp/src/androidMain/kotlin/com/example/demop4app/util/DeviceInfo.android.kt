package com.example.demop4app.util

import android.os.Build

actual class DeviceInfoProvider actual constructor() {
    actual fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            osName = "Android",
            osVersion = Build.VERSION.RELEASE,
            deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}"
        )
    }
}