package com.example.demop4app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.demop4app.data.repository.NotesRepository
import com.example.demop4app.data.repository.NotesRepositoryImpl
import com.example.demop4app.data.repository.SettingsRepository
import com.example.demop4app.data.repository.SettingsRepositoryImpl
import com.example.demop4app.database.NotesDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import okio.Path.Companion.toPath

object AndroidAppContainer {
    private var initialized = false

    lateinit var notesRepository: NotesRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    fun init(context: Context) {
        if (initialized) return

        synchronized(this) {
            if (initialized) return

            val database = NotesDatabase(
                driver = AndroidSqliteDriver(
                    schema = NotesDatabase.Schema,
                    context = context,
                    name = "notes.db"
                )
            )

            notesRepository = NotesRepositoryImpl(database)
            settingsRepository = SettingsRepositoryImpl(createDataStore(context))
            initialized = true
        }
    }

    private fun createDataStore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                context.filesDir
                    .resolve("notes_settings.preferences_pb")
                    .absolutePath
                    .toPath()
            }
        )
    }
}
