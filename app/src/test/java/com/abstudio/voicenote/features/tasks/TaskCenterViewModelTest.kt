package com.abstudio.voicenote.features.tasks

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.repository.TaskRepository
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
class TaskCenterViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    private val taskRepository = mockk<TaskRepository>()
    private val tasksFlow = MutableSharedFlow<List<TaskEntity>>(replay = 1)
    
    private lateinit var viewModel: TaskCenterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { taskRepository.getAllTasksFlow() } returns tasksFlow
        every { taskRepository.getTasks() } returns flow { emit(NetworkResult.Loading()) }
        
        viewModel = TaskCenterViewModel(taskRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadTasks success updates state`() = runTest {
        val mockTasks = listOf(TaskEntity(id = "1", title = "Task 1", description = "Desc", priority = "HIGH", status = "TODO", isDone = false, deadline = null, noteId = null, teamId = null, assignedEntities = null, suggestedActions = null, isSynced = true))
        
        // Emit through the flow that VM is collecting
        tasksFlow.emit(mockTasks)
        advanceUntilIdle()

        assertEquals(mockTasks, viewModel.uiState.value.tasks)
    }

    @Test
    fun `toggleTask success calls repository`() = runTest {
        val taskId = "1"
        val isDone = true
        val mockTask = mockk<TaskEntity>(relaxed = true)
        coEvery { taskRepository.toggleTaskCompletion(taskId, isDone) } returns NetworkResult.Success(mockTask)

        viewModel.toggleTask(taskId, isDone)
        advanceUntilIdle()

        coVerify { taskRepository.toggleTaskCompletion(taskId, isDone) }
    }
}
