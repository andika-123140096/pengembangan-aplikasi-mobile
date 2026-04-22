package com.example.demop4app.util

import platform.UIKit.UIDevice

actual class DeviceInfoProvider actual constructor() {
    actual fun getDeviceInfo(): DeviceInfo {
        val currentDevice = UIDevice.currentDevice
        return DeviceInfo(
            osName = currentDevice.systemName,
            osVersion = currentDevice.systemVersion,
            deviceModel = currentDevice.model
        )
    }
}