package com.example.demop4app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.example.demop4app.di.AndroidAppContainer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        AndroidAppContainer.init(applicationContext)

        setContent {
            App(
                notesRepository = AndroidAppContainer.notesRepository,
                settingsRepository = AndroidAppContainer.settingsRepository
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    Text("Preview requires runtime repositories")
}