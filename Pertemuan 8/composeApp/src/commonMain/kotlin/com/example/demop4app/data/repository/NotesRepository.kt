package com.example.demop4app.data.repository

import com.example.demop4app.data.model.Note
import com.example.demop4app.data.model.SortOrder
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun observeNotes(sortOrder: SortOrder): Flow<List<Note>>
    fun observeNoteById(id: Long): Flow<Note?>
    suspend fun createNote(title: String, content: String)
    suspend fun updateNote(id: Long, title: String, content: String)
    suspend fun deleteNote(id: Long)
    suspend fun toggleFavorite(id: Long)
}
