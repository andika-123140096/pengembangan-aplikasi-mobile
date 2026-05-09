package com.example.demop4app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.demop4app.database.NotesDatabase
import org.koin.core.module.Module
import org.koin.dsl.module
import okio.Path.Companion.toPath
import platform.Foundation.*

actual val platformModule: Module = module {
    single {
        NotesDatabase(
            driver = NativeSqliteDriver(NotesDatabase.Schema, "notes.db")
        )
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null
                )
                (requireNotNull(documentDirectory?.path) + "/notes_settings.preferences_pb").toPath()
            }
        )
    }
}