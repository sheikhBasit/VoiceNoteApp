package com.example.voicenote.features.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicenote.data.api.TasksApi
import com.example.voicenote.data.models.TaskCenterResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TaskCenterState {
    object Loading : TaskCenterState()
    data class Success(val data: TaskCenterResponse) : TaskCenterState()
    data class Error(val message: String) : TaskCenterState()
}

class TasksViewModel(private val tasksApi: TasksApi) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskCenterState>(TaskCenterState.Loading)
    val uiState: StateFlow<TaskCenterState> = _uiState

    init {
        loadTaskCenter()
    }

    fun loadTaskCenter() {
        viewModelScope.launch {
            _uiState.value = TaskCenterState.Loading
            try {
                val response = tasksApi.getTaskCenter()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TaskCenterState.Success(response.body()!!)
                } else {
                    _uiState.value = TaskCenterState.Error("Failed to load Task Center")
                }
            } catch (e: Exception) {
                _uiState.value = TaskCenterState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun toggleTaskCompletion(taskId: String, isDone: Boolean) {
        viewModelScope.launch {
            try {
                val response = tasksApi.toggleTaskCompletion(taskId, mapOf("is_done" to isDone))
                if (response.isSuccessful) {
                    loadTaskCenter() // Refresh list
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
