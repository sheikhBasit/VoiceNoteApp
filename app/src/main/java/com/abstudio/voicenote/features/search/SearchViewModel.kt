package com.abstudio.voicenote.features.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<NoteEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChanged(newQuery: String) {
        _uiState.value = _uiState.value.copy(query = newQuery)
        if (newQuery.length >= 2) {
            search(newQuery)
        } else {
            _uiState.value = _uiState.value.copy(results = emptyList())
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = repository.searchNotes(query)) {
                is NetworkResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        results = result.data ?: emptyList(),
                        isLoading = false
                    )
                }
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        error = result.message,
                        isLoading = false
                    )
                }
                is NetworkResult.Loading -> {
                    // Already handled
                }
            }
        }
    }
}
