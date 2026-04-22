package com.example.demop4app.di

import com.example.demop4app.data.repository.*
import com.example.demop4app.viewmodel.NotesViewModel
import com.example.demop4app.util.DeviceInfoProvider
import com.example.demop4app.util.NetworkMonitor
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

val commonModule = module {
    single<NotesRepository> { NotesRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    single { DeviceInfoProvider() }
    single { NetworkMonitor() }
    factory { NotesViewModel(get(), get()) }
}

expect val platformModule: Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(commonModule, platformModule)
    }
}