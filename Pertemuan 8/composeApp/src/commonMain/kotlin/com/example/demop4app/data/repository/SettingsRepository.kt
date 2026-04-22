package com.example.demop4app.data.repository

import com.example.demop4app.data.model.SortOrder
import com.example.demop4app.data.model.ThemeMode
import com.example.demop4app.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val settings: Flow<UserSettings>

    suspend fun updateThemeMode(themeMode: ThemeMode)
    suspend fun updateSortOrder(sortOrder: SortOrder)
}
