package com.example.demop4app.data.model

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val favoriteNotes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val sortOrder: SortOrder = SortOrder.NEWEST,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
) {
    val isEmpty: Boolean
        get() = !isLoading && notes.isEmpty()
}
