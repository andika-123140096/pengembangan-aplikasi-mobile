package com.example.demop4app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Notes : Screen("notes", "Notes", Icons.Default.List)
    data object Favorites : Screen("favorites", "Favorites", Icons.Default.Favorite)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object Profile : Screen("profile", "Profile", Icons.Default.Person)
    data object NoteDetail : Screen("note_detail/{noteId}", "Note Detail") {
        fun createRoute(noteId: Long) = "note_detail/$noteId"
    }
    data object AddNote : Screen("add_note", "Add Note")
}

val bottomNavItems = listOf(
    Screen.Notes,
    Screen.Favorites,
    Screen.Settings,
    Screen.Profile
)
