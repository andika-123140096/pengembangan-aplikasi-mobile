package com.example.demop4app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demop4app.data.model.Note
import com.example.demop4app.data.model.NotesUiState
import com.example.demop4app.data.model.SortOrder
import com.example.demop4app.data.model.ThemeMode
import com.example.demop4app.data.model.UserSettings
import com.example.demop4app.data.repository.NotesRepository
import com.example.demop4app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesRepository: NotesRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                settingsRepository.settings,
                searchQuery
                    .debounce(250)
                    .distinctUntilChanged()
            ) { settings, query ->
                settings to query
            }
                .flatMapLatest { (settings, query) ->
                    notesRepository.observeNotes(settings.sortOrder)
                        .mapToUiPayload(settings = settings, query = query)
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = throwable.message ?: "Gagal memuat catatan"
                        )
                    }
                }
                .collect { payload ->
                    val filteredNotes = filterBySearch(payload.notes, payload.query)
                    _uiState.update {
                        it.copy(
                            notes = filteredNotes,
                            favoriteNotes = payload.notes.filter { note -> note.isFavorite },
                            searchQuery = payload.query,
                            isLoading = false,
                            errorMessage = null,
                            sortOrder = payload.sortOrder,
                            themeMode = payload.themeMode
                        )
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun createNote(
        title: String,
        content: String,
        onSuccess: () -> Unit = {}
    ) {
        val cleanTitle = title.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isEmpty() || cleanContent.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Judul dan isi catatan wajib diisi")
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                notesRepository.createNote(cleanTitle, cleanContent)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun updateNote(
        id: Long,
        title: String,
        content: String,
        onSuccess: () -> Unit = {}
    ) {
        val cleanTitle = title.trim()
        val cleanContent = content.trim()

        if (cleanTitle.isEmpty() || cleanContent.isEmpty()) {
            _uiState.update {
                it.copy(errorMessage = "Judul dan isi catatan wajib diisi")
            }
            return
        }

        viewModelScope.launch {
            runCatching {
                notesRepository.updateNote(id = id, title = cleanTitle, content = cleanContent)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun deleteNote(id: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            runCatching {
                notesRepository.deleteNote(id)
            }.onSuccess {
                onSuccess()
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun toggleFavorite(id: Long) {
        viewModelScope.launch {
            runCatching {
                notesRepository.toggleFavorite(id)
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            runCatching {
                settingsRepository.updateSortOrder(sortOrder)
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun updateThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            runCatching {
                settingsRepository.updateThemeMode(themeMode)
            }.onFailure { throwable ->
                publishError(throwable)
            }
        }
    }

    fun observeNoteById(id: Long): Flow<Note?> {
        return notesRepository.observeNoteById(id)
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun publishError(throwable: Throwable) {
        _uiState.update {
            it.copy(errorMessage = throwable.message ?: "Operasi gagal dijalankan")
        }
    }

    private fun filterBySearch(notes: List<Note>, query: String): List<Note> {
        if (query.isBlank()) return notes

        return notes.filter { note ->
            note.title.contains(query, ignoreCase = true) ||
                note.content.contains(query, ignoreCase = true)
        }
    }

    private data class UiPayload(
        val notes: List<Note>,
        val query: String,
        val sortOrder: SortOrder,
        val themeMode: ThemeMode
    )

    private fun Flow<List<Note>>.mapToUiPayload(
        settings: UserSettings,
        query: String
    ): Flow<UiPayload> {
        return map { notes ->
            UiPayload(
                notes = notes,
                query = query,
                sortOrder = settings.sortOrder,
                themeMode = settings.themeMode
            )
        }
    }
}
