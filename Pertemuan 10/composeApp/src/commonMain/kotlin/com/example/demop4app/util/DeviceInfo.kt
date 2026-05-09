package com.example.demop4app.util

data class DeviceInfo(
    val osName: String,
    val osVersion: String,
    val deviceModel: String
)

expect class DeviceInfoProvider() {
    fun getDeviceInfo(): DeviceInfo
}