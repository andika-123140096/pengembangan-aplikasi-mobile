package com.example.demop4app

import androidx.compose.ui.window.ComposeUIViewController
import com.example.demop4app.di.IosAppContainer

fun MainViewController() = ComposeUIViewController {
	IosAppContainer.init()
	App(
		notesRepository = IosAppContainer.notesRepository,
		settingsRepository = IosAppContainer.settingsRepository
	)
}