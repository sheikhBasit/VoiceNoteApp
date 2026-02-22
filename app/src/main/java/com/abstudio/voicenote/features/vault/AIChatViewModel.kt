package com.abstudio.voicenote.features.vault

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.api.AiApi
import com.abstudio.voicenote.data.models.AiAskRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(val text: String, val isUser: Boolean)

sealed class AIChatState {
    object Idle : AIChatState()
    object Loading : AIChatState()
    data class Success(val answer: String) : AIChatState()
    data class Error(val message: String) : AIChatState()
}

@HiltViewModel
class AIChatViewModel @Inject constructor(
    private val aiApi: AiApi
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("Hello! I'm your VoiceNote Assistant. I can answer questions across all your meetings.", false)
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _state = MutableStateFlow<AIChatState>(AIChatState.Idle)
    val state: StateFlow<AIChatState> = _state

    fun sendMessage(question: String) {
        if (question.isBlank()) return

        // 1. Add user message
        _messages.value = _messages.value + ChatMessage(question, true)
        
        // 2. Call AI
        viewModelScope.launch {
            _state.value = AIChatState.Loading
            try {
                val response = aiApi.askAi(AiAskRequest(question))
                if (response.isSuccessful && response.body() != null) {
                    val answer = response.body()!!.answer
                    _messages.value = _messages.value + ChatMessage(answer, false)
                    _state.value = AIChatState.Success(answer)
                } else {
                    val errorMsg = "Sorry, I couldn't process that. Error: ${response.code()}"
                    _messages.value = _messages.value + ChatMessage(errorMsg, false)
                    _state.value = AIChatState.Error(errorMsg)
                }
            } catch (e: Exception) {
                val errorMsg = "Connection error. Please try again later."
                _messages.value = _messages.value + ChatMessage(errorMsg, false)
                _state.value = AIChatState.Error(errorMsg)
            }
        }
    }
}
