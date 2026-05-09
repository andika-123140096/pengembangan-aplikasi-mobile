package com.example.demop4app.data.repository

import com.example.demop4app.database.NotesDatabase
import com.example.demop4app.database.NotesDatabaseQueries
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class NotesRepositoryTest {

    private lateinit var database: NotesDatabase
    private lateinit var queries: NotesDatabaseQueries
    private lateinit var repository: NotesRepository

    @BeforeTest
    fun setup() {
        database = mockk(relaxed = true)
        queries = mockk(relaxed = true)
        every { database.notesDatabaseQueries } returns queries
        repository = NotesRepositoryImpl(database)
    }

    @Test
    fun `createNote should call insertNote with correct parameters`() = runTest {
        // Arrange
        val title = "Test Title"
        val content = "Test Content"

        // Act
        repository.createNote(title, content)

        // Assert
        verify {
            queries.insertNote(
                title = title,
                content = content,
                created_at = any(),
                updated_at = any(),
                is_favorite = 0L
            )
        }
        confirmVerified(queries)
    }

    @Test
    fun `updateNote should call updateNote with correct parameters`() = runTest {
        // Arrange
        val id = 1L
        val title = "Updated Title"
        val content = "Updated Content"

        // Act
        repository.updateNote(id, title, content)

        // Assert
        verify {
            queries.updateNote(
                title = title,
                content = content,
                updated_at = any(),
                id = id
            )
        }
        confirmVerified(queries)
    }

    @Test
    fun `deleteNote should call deleteNote query`() = runTest {
        // Arrange
        val id = 1L

        // Act
        repository.deleteNote(id)

        // Assert
        verify { queries.deleteNote(id) }
        confirmVerified(queries)
    }

    @Test
    fun `toggleFavorite should call toggleFavorite query`() = runTest {
        // Arrange
        val id = 1L

        // Act
        repository.toggleFavorite(id)

        // Assert
        verify {
            queries.toggleFavorite(
                updated_at = any(),
                id = id
            )
        }
        confirmVerified(queries)
    }

    @Test
    fun `observeNoteById should call selectById query`() = runTest {
        // Arrange
        val id = 1L

        // Act
        repository.observeNoteById(id)

        // Assert
        verify { queries.selectById(id) }
    }
}
