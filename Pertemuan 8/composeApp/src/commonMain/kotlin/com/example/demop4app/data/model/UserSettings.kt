package com.example.demop4app.data.model

data class UserSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val sortOrder: SortOrder = SortOrder.NEWEST
)
