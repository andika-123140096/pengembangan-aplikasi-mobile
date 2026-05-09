package com.example.demop4app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.demop4app.database.NotesDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import okio.Path.Companion.toPath

actual val platformModule: Module = module {
    single {
        NotesDatabase(
            driver = AndroidSqliteDriver(NotesDatabase.Schema, androidContext(), "notes.db")
        )
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                androidContext().filesDir.resolve("notes_settings.preferences_pb").absolutePath.toPath()
            }
        )
    }
}