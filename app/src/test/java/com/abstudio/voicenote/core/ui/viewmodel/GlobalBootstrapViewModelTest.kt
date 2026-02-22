package com.abstudio.voicenote.core.ui.viewmodel

import com.abstudio.voicenote.data.repository.NoteRepository
import com.abstudio.voicenote.data.repository.TaskRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GlobalBootstrapViewModelTest {

    private val noteRepository = mockk<NoteRepository>(relaxed = true)
    private val taskRepository = mockk<TaskRepository>(relaxed = true)
    private lateinit var viewModel: GlobalBootstrapViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = GlobalBootstrapViewModel(noteRepository, taskRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `bootstrap should set isDataWarmed to true when finished`() = runTest {
        coEvery { noteRepository.getDashboardMetrics() } returns com.abstudio.voicenote.core.util.NetworkResult.Success(mockk())
        coEvery { taskRepository.getTasksDueToday() } returns com.abstudio.voicenote.core.util.NetworkResult.Success(emptyList())

        viewModel.bootstrap()
        advanceUntilIdle()

        assertTrue(viewModel.isDataWarmed.value)
    }

    @Test
    fun `setError should update globalError state`() = runTest {
        val errorMessage = "Network Failure"
        viewModel.setError(errorMessage)
        
        assertEquals(errorMessage, viewModel.globalError.value)
    }

    @Test
    fun `clearError should set globalError to null`() = runTest {
        viewModel.setError("Some Error")
        viewModel.clearError()
        
        assertEquals(null, viewModel.globalError.value)
    }
}
