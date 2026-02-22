package com.abstudio.voicenote.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TaskCenterUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val tasksDueToday: Int = 0,
    val tasksOverdue: Int = 0
)

@HiltViewModel
class TaskCenterViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskCenterUiState())
    val uiState: StateFlow<TaskCenterUiState> = _uiState

    init {
        loadTasks()
    }

    internal fun loadTasks() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            // 1. Trigger network refresh (fire and forget or collect)
            // We can collect the flow from repository which combines local + network status
            // But here let's just use getAllTasksFlow from DAO for UI and trigger network fetch separately
            
            launch {
                taskRepository.getTasks().collect { result ->
                     // Handle network result if needed (e.g. show sync error)
                     if (result is NetworkResult.Error<*>) {
                         _uiState.value = _uiState.value.copy(error = result.message)
                     }
                }
            }

            // 2. Observe Local DB
            taskRepository.getAllTasksFlow().collect { tasks ->
                val sortedTasks = tasks.sortedWith(compareByDescending<TaskEntity> { it.priority }.thenBy { it.deadline })
                val dueTodayCount = tasks.count { isDueToday(it.deadline) && !it.isDone }
                val overdueCount = tasks.count { isOverdue(it.deadline) && !it.isDone }
                
                _uiState.value = _uiState.value.copy(
                    tasks = sortedTasks,
                    isLoading = false,
                    tasksDueToday = dueTodayCount,
                    tasksOverdue = overdueCount
                )
            }
        }
    }

    fun toggleTask(taskId: String, isDone: Boolean) {
        viewModelScope.launch {
            // Optimistic update could be done here on local list, but we rely on repository/db flow
            val result = taskRepository.toggleTaskCompletion(taskId, isDone)
            if (result is NetworkResult.Error<*>) {
                _uiState.value = _uiState.value.copy(error = result.message)
            }
        }
    }

    private fun isDueToday(deadline: Long?): Boolean {
        // Simple logic for checking if deadline is today
        if (deadline == null) return false
        val today = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        val tomorrow = java.time.LocalDate.now().plusDays(1).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
        return deadline in today until tomorrow
    }

    private fun isOverdue(deadline: Long?): Boolean {
        if (deadline == null) return false
        val now = System.currentTimeMillis()
        return deadline < now
    }
}
