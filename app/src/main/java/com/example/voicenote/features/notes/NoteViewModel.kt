package com.example.voicenote.features.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicenote.data.api.NotesApi
import com.example.voicenote.data.models.NoteDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NoteDetailState {
    object Loading : NoteDetailState()
    data class Success(val note: NoteDetailResponse) : NoteDetailState()
    data class Error(val message: String) : NoteDetailState()
}

class NoteViewModel(private val notesApi: NotesApi) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteDetailState>(NoteDetailState.Loading)
    val uiState: StateFlow<NoteDetailState> = _uiState

    fun loadNoteDetail(noteId: String) {
        viewModelScope.launch {
            _uiState.value = NoteDetailState.Loading
            try {
                val response = notesApi.getNoteDetail(noteId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = NoteDetailState.Success(response.body()!!)
                } else {
                    _uiState.value = NoteDetailState.Error("Failed to load note details")
                }
            } catch (e: Exception) {
                _uiState.value = NoteDetailState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
