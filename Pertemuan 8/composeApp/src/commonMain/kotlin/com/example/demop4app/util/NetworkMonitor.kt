package com.example.demop4app.util

import kotlinx.coroutines.flow.StateFlow

expect class NetworkMonitor() {
    val isConnected: StateFlow<Boolean>
}