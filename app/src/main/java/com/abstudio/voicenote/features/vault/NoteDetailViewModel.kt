package com.abstudio.voicenote.features.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.abstudio.voicenote.core.analytics.AnalyticsTracker
import com.abstudio.voicenote.data.api.NotesApi
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.repository.TaskRepository

data class NoteDetailUiState(
    val isLoading: Boolean = false,
    val note: NoteEntity? = null,
    val tasks: List<TaskEntity> = emptyList(),
    val error: String? = null,
    val chatMessages: List<ChatMessage> = emptyList(),
    val isChatLoading: Boolean = false
)

@HiltViewModel
class NoteDetailViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val taskRepository: TaskRepository,
    private val notesApi: NotesApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteDetailUiState())
    val uiState: StateFlow<NoteDetailUiState> = _uiState.asStateFlow()

    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val noteDeferred = async { noteRepository.getNote(noteId) }
            val tasksDeferred = async { taskRepository.getTasks().first() }

            val noteResult = noteDeferred.await()
            val tasksResult = tasksDeferred.await()

            when (noteResult) {
                is NetworkResult.Success -> {
                    val tasks = if (tasksResult is NetworkResult.Success) {
                        tasksResult.data?.filter { it.noteId == noteId } ?: emptyList()
                    } else emptyList()

                    _uiState.update { it.copy(isLoading = false, note = noteResult.data, tasks = tasks) }
                }
                is NetworkResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = noteResult.message) }
                }
                is NetworkResult.Loading -> {
                     _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun toggleTask(taskId: String, isDone: Boolean) {
        viewModelScope.launch {
            val result = taskRepository.toggleTaskCompletion(taskId, isDone)
            if (result is NetworkResult.Success) {
                _uiState.update { state ->
                    state.copy(tasks = state.tasks.map {
                        if (it.id == taskId) it.copy(isDone = isDone) else it
                    })
                }
            }
        }
    }

    fun askNoteQuestion(noteId: String, question: String) {
        viewModelScope.launch {
            AnalyticsTracker.trackNoteChat(noteId)
            // Add user message immediately
            _uiState.update { state ->
                state.copy(
                    chatMessages = state.chatMessages + ChatMessage(question, isUser = true),
                    isChatLoading = true
                )
            }

            try {
                val response = notesApi.askAboutNote(noteId, mapOf("question" to question))
                if (response.isSuccessful) {
                    val answer = response.body()?.get("answer") ?: "No response"
                    _uiState.update { state ->
                        state.copy(
                            chatMessages = state.chatMessages + ChatMessage(answer, isUser = false),
                            isChatLoading = false
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            chatMessages = state.chatMessages + ChatMessage(
                                "Error: ${response.message()}", isUser = false
                            ),
                            isChatLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        chatMessages = state.chatMessages + ChatMessage(
                            "Error: ${e.message ?: "Failed to get answer"}", isUser = false
                        ),
                        isChatLoading = false
                    )
                }
            }
        }
    }
}
