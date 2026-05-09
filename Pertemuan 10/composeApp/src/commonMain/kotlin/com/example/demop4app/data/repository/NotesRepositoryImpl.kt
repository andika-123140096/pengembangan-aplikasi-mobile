package com.example.demop4app.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.example.demop4app.data.model.Note
import com.example.demop4app.data.model.SortOrder
import com.example.demop4app.database.Notes
import com.example.demop4app.database.NotesDatabase
import com.example.demop4app.util.currentTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NotesRepositoryImpl(
    private val database: NotesDatabase
) : NotesRepository {

    private val queries = database.notesDatabaseQueries

    override fun observeNotes(sortOrder: SortOrder): Flow<List<Note>> {
        val query = when (sortOrder) {
            SortOrder.NEWEST -> queries.selectAllNewest()
            SortOrder.OLDEST -> queries.selectAllOldest()
            SortOrder.TITLE_ASC -> queries.selectAllTitleAsc()
        }

        return query
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { notes -> notes.map { it.toModel() } }
    }

    override fun observeNoteById(id: Long): Flow<Note?> {
        return queries.selectById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Default)
            .map { it?.toModel() }
    }

    override suspend fun createNote(title: String, content: String) {
        val now = currentTimeMillis()
        queries.insertNote(
            title = title,
            content = content,
            created_at = now,
            updated_at = now,
            is_favorite = 0L
        )
    }

    override suspend fun updateNote(id: Long, title: String, content: String) {
        queries.updateNote(
            title = title,
            content = content,
            updated_at = currentTimeMillis(),
            id = id
        )
    }

    override suspend fun deleteNote(id: Long) {
        queries.deleteNote(id)
    }

    override suspend fun toggleFavorite(id: Long) {
        queries.toggleFavorite(
            updated_at = currentTimeMillis(),
            id = id
        )
    }
}

private fun Notes.toModel(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = created_at,
        updatedAt = updated_at,
        isFavorite = is_favorite == 1L
    )
}
