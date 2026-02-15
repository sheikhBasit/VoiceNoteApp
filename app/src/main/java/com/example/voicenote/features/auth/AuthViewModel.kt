package com.example.voicenote.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicenote.data.api.AuthApi
import com.example.voicenote.data.models.UserSyncRequest
import com.example.voicenote.data.models.UserSyncResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: UserSyncResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(private val authApi: AuthApi) : ViewModel() {

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun syncDevice(email: String, deviceId: String, deviceName: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val response = authApi.syncDevice(
                    UserSyncRequest(email, deviceId, deviceName)
                )
                if (response.isSuccessful && response.body() != null) {
                    _state.value = AuthState.Success(response.body()!!)
                } else {
                    _state.value = AuthState.Error("Sync failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
