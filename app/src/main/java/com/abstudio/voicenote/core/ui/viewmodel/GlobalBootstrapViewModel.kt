package com.abstudio.voicenote.core.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.repository.NoteRepository
import com.abstudio.voicenote.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalBootstrapViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _isDataWarmed = MutableStateFlow(false)
    val isDataWarmed: StateFlow<Boolean> = _isDataWarmed

    private val _globalError = MutableStateFlow<String?>(null)
    val globalError: StateFlow<String?> = _globalError

    init {
        bootstrap()
    }

    fun setError(message: String?) {
        _globalError.value = message
    }

    fun clearError() {
        _globalError.value = null
    }

    fun bootstrap() {
        viewModelScope.launch {
            // Eagerly fetch dashboard data
            val dashboardJob = async { noteRepository.getDashboardMetrics() }
            val tasksJob = async { taskRepository.getTasksDueToday() }
            
            // Collect the first results but don't block the UI
            dashboardJob.await()
            tasksJob.await()
            
            _isDataWarmed.value = true
        }
    }
    
    /**
     * Called when user enters a list view to pre-fetch details.
     */
    fun onEnterVault(noteIds: List<String>) {
        viewModelScope.launch {
            noteRepository.warmCache(noteIds)
        }
    }
}
