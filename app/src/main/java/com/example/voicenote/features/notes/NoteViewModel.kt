package com.example.voicenote.features.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicenote.data.api.NotesApi
import com.example.voicenote.data.models.NoteDetailResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class NoteDetailState {
    object Idle : NoteDetailState()
    object Loading : NoteDetailState()
    data class Success(val note: NoteDetailResponse) : NoteDetailState()
    data class Error(val message: String) : NoteDetailState()
}

sealed class NotesListState {
    object Loading : NotesListState()
    data class Success(val notes: List<NoteDetailResponse>) : NotesListState()
    data class Error(val message: String) : NotesListState()
}

class NoteViewModel(private val notesApi: NotesApi) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteDetailState>(NoteDetailState.Idle)
    val uiState: StateFlow<NoteDetailState> = _uiState

    private val _notesState = MutableStateFlow<NotesListState>(NotesListState.Loading)
    val notesState: StateFlow<NotesListState> = _notesState

    fun loadNotes() {
        viewModelScope.launch {
            _notesState.value = NotesListState.Loading
            try {
                val response = notesApi.getNotes()
                if (response.isSuccessful && response.body() != null) {
                    _notesState.value = NotesListState.Success(response.body()!!)
                } else {
                    _notesState.value = NotesListState.Error("Failed to load notes")
                }
            } catch (e: Exception) {
                _notesState.value = NotesListState.Error(e.message ?: "Unknown error")
            }
        }
    }

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
