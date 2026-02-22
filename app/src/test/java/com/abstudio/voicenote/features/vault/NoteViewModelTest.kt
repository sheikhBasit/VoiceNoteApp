package com.abstudio.voicenote.features.vault

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.models.NoteDetailResponse
import com.abstudio.voicenote.core.network.SSEManager
import com.abstudio.voicenote.data.repository.NoteRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private val noteRepository = mockk<NoteRepository>()
    private val sseManager = mockk<SSEManager>(relaxed = true)
    private val localNotesFlow = MutableSharedFlow<List<NoteEntity>>(replay = 1)
    
    private lateinit var viewModel: NoteViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { noteRepository.getAllNotesFlow() } returns localNotesFlow
        every { noteRepository.getNotes(any(), any()) } returns flow { emit(NetworkResult.Loading()) }
        
        viewModel = NoteViewModel(noteRepository, sseManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadNotes success and collection`() = runTest {
        val mockNotes = listOf(NoteEntity(id = "1", title = "Note 1", summary = "Summary 1", transcript = "", status = "DONE", timestamp = 123, audioPath = null, localAudioPath = null, isSynced = true))
        
        localNotesFlow.emit(mockNotes)
        advanceUntilIdle()

        assertEquals(mockNotes, viewModel.uiState.value.notes)
    }

    @Test
    fun `onSearchQueryChanged updates state`() = runTest {
        val query = "test"
        val mockResults = listOf(NoteEntity(id = "1", title = "Test Note", summary = "Summary", transcript = "", status = "DONE", timestamp = 123, audioPath = null, localAudioPath = null, isSynced = true))
        coEvery { noteRepository.searchNotes(query) } returns NetworkResult.Success(mockResults)

        viewModel.onSearchQueryChanged(query)
        advanceUntilIdle()

        assertEquals(mockResults, viewModel.uiState.value.notes)
    }

    @Test
    fun `loadNoteDetail success`() = runTest {
        val noteId = "123"
        val mockDetail = mockk<NoteDetailResponse>(relaxed = true)
        every { noteRepository.getNoteById(noteId) } returns flow {
            emit(NetworkResult.Success(mockDetail))
        }

        viewModel.loadNoteDetail(noteId)
        advanceUntilIdle()

        assertTrue(viewModel.detailState.value is NoteDetailState.Success)
    }
}
