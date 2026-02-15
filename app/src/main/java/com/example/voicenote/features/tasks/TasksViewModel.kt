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
}
