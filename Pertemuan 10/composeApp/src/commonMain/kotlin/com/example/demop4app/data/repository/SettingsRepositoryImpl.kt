package com.example.demop4app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.demop4app.data.model.SortOrder
import com.example.demop4app.data.model.ThemeMode
import com.example.demop4app.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    override val settings: Flow<UserSettings> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            UserSettings(
                themeMode = preferences[THEME_MODE_KEY]
                    ?.let(ThemeMode::valueOf)
                    ?: ThemeMode.SYSTEM,
                sortOrder = preferences[SORT_ORDER_KEY]
                    ?.let(SortOrder::valueOf)
                    ?: SortOrder.NEWEST
            )
        }

    override suspend fun updateThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[THEME_MODE_KEY] = themeMode.name
        }
    }

    override suspend fun updateSortOrder(sortOrder: SortOrder) {
        dataStore.edit { preferences ->
            preferences[SORT_ORDER_KEY] = sortOrder.name
        }
    }

    private companion object {
        val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
        val SORT_ORDER_KEY = stringPreferencesKey("sort_order")
    }
}
