package com.abstudio.voicenote.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.models.DashboardStats
import com.abstudio.voicenote.data.models.NoteSummary
import com.abstudio.voicenote.data.models.TaskResponse
import com.abstudio.voicenote.data.repository.NoteRepository
import com.abstudio.voicenote.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val recentNotes: List<NoteSummary> = emptyList(),
    val tasksDueToday: List<NoteTaskItem> = emptyList(),
    val aiInsights: List<com.abstudio.voicenote.data.models.AIInsight> = emptyList(),
    val isRecording: Boolean = false,
    val error: String? = null
)

// UI Model for dashboard tasks (simplified from Entity/Response)
data class NoteTaskItem(
    val id: String,
    val description: String,
    val priority: String,
    val isDone: Boolean
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeRecordingState()
    }

    private fun observeRecordingState() {
        viewModelScope.launch {
            com.abstudio.voicenote.features.recording.VoiceRecorderService.isRecordingFlow.collect { recording ->
                _uiState.update { it.copy(isRecording = recording) }
            }
        }
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Parallel fetch
            val dashboardDeferred = async { noteRepository.getDashboardMetrics() }
            val tasksDeferred = async { taskRepository.getTasksDueToday() }
            
            val dashboardResult = dashboardDeferred.await()
            val tasksResult = tasksDeferred.await()

            val newState = _uiState.value.copy(isLoading = false, error = null)
            
            if (dashboardResult is NetworkResult.Success) {
                dashboardResult.data?.let { data ->
                    _uiState.update { 
                        it.copy(
                            stats = data.stats,
                            recentNotes = data.recentNotes,
                            aiInsights = data.aiInsights
                        )
                    }
                } ?: run {
                    _uiState.update { it.copy(error = "Dashboard data is empty") }
                }
            } 
            
            if (tasksResult is NetworkResult.Success) {
                tasksResult.data?.let { tasks ->
                    _uiState.update {
                        it.copy(
                            tasksDueToday = tasks.map { task ->
                                 NoteTaskItem(
                                     id = task.id,
                                     description = task.description,
                                     priority = task.priority,
                                     isDone = task.isDone
                                 )
                            }
                        )
                    }
                }
            }
            
            if (dashboardResult is NetworkResult.Error) {
                 _uiState.update { it.copy(error = dashboardResult.message) }
            }
        }
    }
}
