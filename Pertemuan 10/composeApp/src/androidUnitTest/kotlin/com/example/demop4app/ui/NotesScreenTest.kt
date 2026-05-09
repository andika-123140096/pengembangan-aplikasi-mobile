package com.example.demop4app.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import com.example.demop4app.data.model.Note
import com.example.demop4app.data.model.NotesUiState
import com.example.demop4app.util.NetworkMonitor
import com.example.demop4app.viewmodel.NotesViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.runner.RunWith
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NotesScreenTest {

    private lateinit var viewModel: NotesViewModel
    private lateinit var networkMonitor: NetworkMonitor
    private val uiStateFlow = MutableStateFlow(NotesUiState())

    @BeforeTest
    fun setup() {
        viewModel = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true)
        
        every { viewModel.uiState } returns uiStateFlow
        every { networkMonitor.isConnected } returns MutableStateFlow(true)

        startKoin {
            modules(module {
                single { networkMonitor }
            })
        }
    }

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `FAB add note should be displayed`() = runComposeUiTest {
        setContent {
            KoinContext {
                NotesScreen(
                    viewModel = viewModel,
                    onNoteClick = {},
                    onAddNoteClick = {}
                )
            }
        }

        onNodeWithContentDescription("Add Note").assertExists()
    }

    @Test
    fun `empty state message should be shown when no notes`() = runComposeUiTest {
        uiStateFlow.value = NotesUiState(notes = emptyList(), isLoading = false)

        setContent {
            KoinContext {
                NotesScreen(
                    viewModel = viewModel,
                    onNoteClick = {},
                    onAddNoteClick = {}
                )
            }
        }

        onNodeWithText("Belum ada catatan. Tekan tombol + untuk menambah.").assertExists()
    }

    @Test
    fun `note items should be displayed when notes list is not empty`() = runComposeUiTest {
        val testNotes = listOf(
            Note(id = 1, title = "Test Note", content = "Test Content", createdAt = 0, updatedAt = 0, isFavorite = false)
        )
        uiStateFlow.value = NotesUiState(notes = testNotes, isLoading = false)

        setContent {
            KoinContext {
                NotesScreen(
                    viewModel = viewModel,
                    onNoteClick = {},
                    onAddNoteClick = {}
                )
            }
        }

        onNodeWithText("Test Note").assertExists()
        onNodeWithText("Test Content").assertExists()
    }
}
