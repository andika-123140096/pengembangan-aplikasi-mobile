package com.example.demop4app

import androidx.compose.ui.window.ComposeUIViewController
import com.example.demop4app.di.initKoin

fun MainViewController() = ComposeUIViewController(
	configure = {
		initKoin()
	}
) {
	App()
}