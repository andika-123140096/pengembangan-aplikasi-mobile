package com.example.demop4app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.example.demop4app.data.repository.NotesRepository
import com.example.demop4app.data.repository.NotesRepositoryImpl
import com.example.demop4app.data.repository.SettingsRepository
import com.example.demop4app.data.repository.SettingsRepositoryImpl
import com.example.demop4app.database.NotesDatabase
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

object IosAppContainer {
    private var initialized = false

    lateinit var notesRepository: NotesRepository
        private set

    lateinit var settingsRepository: SettingsRepository
        private set

    fun init() {
        if (initialized) return

        val database = NotesDatabase(
            driver = NativeSqliteDriver(
                schema = NotesDatabase.Schema,
                name = "notes.db"
            )
        )

        notesRepository = NotesRepositoryImpl(database)
        settingsRepository = SettingsRepositoryImpl(createDataStore())
        initialized = true
    }

    private fun createDataStore(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                val filePath = requireNotNull(documentDirectory?.path) + "/notes_settings.preferences_pb"
                filePath.toPath()
            }
        )
    }
}
