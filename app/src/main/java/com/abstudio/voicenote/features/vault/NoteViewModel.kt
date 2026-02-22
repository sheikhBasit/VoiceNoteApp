package com.abstudio.voicenote.features.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.api.NotesApi
import com.abstudio.voicenote.data.models.NoteDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VaultUiState(
    val isLoading: Boolean = false,
    val notes: List<NoteEntity> = emptyList(),
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class NoteDetailState {
    object Idle : NoteDetailState()
    object Loading : NoteDetailState()
    data class Success(val note: NoteDetailResponse) : NoteDetailState()
    data class Error(val message: String) : NoteDetailState()
}

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteRepository: NoteRepository,
    private val sseManager: com.abstudio.voicenote.core.network.SSEManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    private val _detailState = MutableStateFlow<NoteDetailState>(NoteDetailState.Idle)
    val detailState: StateFlow<NoteDetailState> = _detailState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadNotes()
        observeLocalNotes()
        observeSseEvents()
    }

    private fun observeSseEvents() {
        sseManager.connect(
            url = "${com.abstudio.voicenote.BuildConfig.BASE_URL}api/v1/sse/events",
            listener = object : com.abstudio.voicenote.core.network.SSEManager.SSEListener {
                override fun onOpen() {}
                override fun onEvent(type: String?, data: String) {
                    viewModelScope.launch {
                         // On any note-related event, refresh the list
                         // Specific types could be "note_updated", "transcription_finished"
                         loadNotes()
                    }
                }
                override fun onClosed() {}
                override fun onError(t: Throwable?) {}
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        sseManager.disconnect()
    }

    private fun observeLocalNotes() {
        viewModelScope.launch {
            noteRepository.getAllNotesFlow().collectLatest { notes ->
                if (_uiState.value.searchQuery.isEmpty()) {
                    _uiState.update { it.copy(notes = notes) }
                }
            }
        }
    }

    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Trigger network fetch
            noteRepository.getNotes(skip = 0, limit = 50).collect { result ->
                 when (result) {
                    is NetworkResult.Success<*> -> {
                        _uiState.update { it.copy(isLoading = false, error = null) }
                    }
                    is NetworkResult.Error<*> -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is NetworkResult.Loading<*> -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // Debounce
            if (query.isNotEmpty()) {
                _uiState.update { it.copy(isLoading = true) }
                val result = noteRepository.searchNotes(query)
                if (result is NetworkResult.Success<*>) {
                    _uiState.update { it.copy(notes = result.data ?: emptyList(), isLoading = false) }
                } else if (result is NetworkResult.Error<*>) {
                    _uiState.update { it.copy(error = result.message, isLoading = false) }
                }
            } else {
                 _uiState.update { it.copy(isLoading = false) }
                 // Layout will update from observeLocalNotes automatically
            }
        }
    }

    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            _detailState.value = NoteDetailState.Loading
            noteRepository.getNoteById(noteId).collect { result ->
                when (result) {
                    is NetworkResult.Success<*> -> {
                        result.data?.let {
                            _detailState.value = NoteDetailState.Success(it as com.abstudio.voicenote.data.models.NoteDetailResponse)
                        } ?: run {
                            _detailState.value = NoteDetailState.Error("Note not found")
                        }
                    }
                    is NetworkResult.Error<*> -> {
                        _detailState.value = NoteDetailState.Error(result.message ?: "Unknown error")
                    }
                    is NetworkResult.Loading<*> -> {
                        _detailState.value = NoteDetailState.Loading
                    }
                }
            }
        }
    }
}
