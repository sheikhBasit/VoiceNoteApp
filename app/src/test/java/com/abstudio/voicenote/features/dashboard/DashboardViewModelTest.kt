package com.abstudio.voicenote.features.dashboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.models.DashboardResponse
import com.abstudio.voicenote.data.models.NoteSummary
import com.abstudio.voicenote.data.models.TaskResponse
import com.abstudio.voicenote.data.repository.NoteRepository
import com.abstudio.voicenote.data.repository.TaskRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private val noteRepository = mockk<NoteRepository>()
    private val taskRepository = mockk<TaskRepository>()
    private lateinit var viewModel: DashboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        // Delay VM initialization to each test or provide default mocks here
        coEvery { noteRepository.getDashboardMetrics() } returns NetworkResult.Loading()
        coEvery { taskRepository.getTasksDueToday() } returns NetworkResult.Loading()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadDashboardData updates state with combined results`() = runTest {
        val mockDashboard = DashboardResponse(
            stats = mockk(relaxed = true),
            recentNotes = listOf(NoteSummary(id = "1", title = "Note 1", timestamp = 1, status = "SY")),
            aiInsights = emptyList()
        )
        val mockTasks = listOf(TaskEntity(id = "t1", noteId = null, title = "Task", description = "Task", priority = "HIGH", status = "TODO", isDone = false, deadline = null, teamId = null, assignedEntities = null, suggestedActions = null, isSynced = true))

        coEvery { noteRepository.getDashboardMetrics() } returns NetworkResult.Success(mockDashboard)
        coEvery { taskRepository.getTasksDueToday() } returns NetworkResult.Success(mockTasks)

        viewModel = DashboardViewModel(noteRepository, taskRepository)
        advanceUntilIdle()

        assertEquals(mockDashboard.recentNotes, viewModel.uiState.value.recentNotes)
        assertEquals(1, viewModel.uiState.value.tasksDueToday.size)
        assertEquals("Task", viewModel.uiState.value.tasksDueToday[0].description)
    }

    @Test
    fun `loadDashboardData error handles failure`() = runTest {
        coEvery { noteRepository.getDashboardMetrics() } returns NetworkResult.Error("API Fail")
        coEvery { taskRepository.getTasksDueToday() } returns NetworkResult.Success(emptyList())

        viewModel = DashboardViewModel(noteRepository, taskRepository)
        advanceUntilIdle()

        assertEquals("API Fail", viewModel.uiState.value.error)
    }
}
