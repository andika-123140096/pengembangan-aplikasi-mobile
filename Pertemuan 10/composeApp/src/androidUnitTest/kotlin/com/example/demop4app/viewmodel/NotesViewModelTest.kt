package com.example.demop4app.viewmodel

import app.cash.turbine.test
import com.example.demop4app.data.model.Note
import com.example.demop4app.data.model.SortOrder
import com.example.demop4app.data.model.UserSettings
import com.example.demop4app.data.repository.NotesRepository
import com.example.demop4app.data.repository.SettingsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {

    private lateinit var notesRepository: NotesRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: NotesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        notesRepository = mockk(relaxed = true)
        settingsRepository = mockk(relaxed = true)

        every { settingsRepository.settings } returns flowOf(UserSettings())
        every { notesRepository.observeNotes(any()) } returns flowOf(emptyList())

        viewModel = NotesViewModel(notesRepository, settingsRepository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- Unit Tests (MockK) ---

    @Test
    fun `createNote with valid input should call repository`() = runTest {
        val title = "Valid Title"
        val content = "Valid Content"

        viewModel.createNote(title, content)
        advanceUntilIdle()

        coVerify { notesRepository.createNote(title, content) }
    }

    @Test
    fun `createNote with empty title should show error`() = runTest {
        viewModel.createNote("", "Content")
        // No advanceUntilIdle needed because error message update is synchronous in ViewModel

        assertEquals("Judul dan isi catatan wajib diisi", viewModel.uiState.value.errorMessage)
        coVerify(exactly = 0) { notesRepository.createNote(any(), any()) }
    }

    @Test
    fun `deleteNote should call repository`() = runTest {
        val id = 1L
        viewModel.deleteNote(id)
        advanceUntilIdle()

        coVerify { notesRepository.deleteNote(id) }
    }

    @Test
    fun `updateSortOrder should call settings repository`() = runTest {
        val sortOrder = SortOrder.TITLE_ASC
        viewModel.updateSortOrder(sortOrder)
        advanceUntilIdle()

        coVerify { settingsRepository.updateSortOrder(sortOrder) }
    }

    // --- Flow Tests (Turbine) ---

    @Test
    fun `uiState should emit initial state and then updated notes`() = runTest {
        val mockNotes = listOf(
            Note(1, "Note 1", "Content 1", 0, 0, false)
        )
        every { notesRepository.observeNotes(any()) } returns flowOf(mockNotes)

        // Re-init viewModel to capture emissions from init { observeData() }
        viewModel = NotesViewModel(notesRepository, settingsRepository)

        viewModel.uiState.test {
            val initialState = awaitItem()
            // Initial state might be default, then observeData updates it
            val updatedState = awaitItem()
            assertEquals(mockNotes, updatedState.notes)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSearchQueryChange should update uiState and filter results`() = runTest {
        val mockNotes = listOf(
            Note(1, "Apple", "Fruit", 0, 0, false),
            Note(2, "Banana", "Fruit", 0, 0, false)
        )
        every { notesRepository.observeNotes(any()) } returns flowOf(mockNotes)
        viewModel = NotesViewModel(notesRepository, settingsRepository)

        viewModel.uiState.test {
            // Initial emissions
            skipItems(2) // Default + first load

            viewModel.onSearchQueryChange("Apple")

            val searchState = awaitItem()
            assertEquals("Apple", searchState.searchQuery)
            
            // Wait for debounce and filter (250ms debounce in ViewModel)
            // In runTest with StandardTestDispatcher, we might need to advance time
            // but the filter logic in collect should trigger.
            
            val filteredState = awaitItem()
            assertEquals(1, filteredState.notes.size)
            assertEquals("Apple", filteredState.notes[0].title)
            
            cancelAndIgnoreRemainingEvents()
        }
    }
}
