package com.abstudio.voicenote.features.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.api.AuthApi
import com.abstudio.voicenote.data.models.UserSyncRequest
import com.abstudio.voicenote.data.models.UserSyncResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import com.abstudio.voicenote.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import com.abstudio.voicenote.features.auth.GoogleAuthHelper
import retrofit2.Response
import com.abstudio.voicenote.data.models.UserInfo

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: UserSyncResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val googleAuthHelper: GoogleAuthHelper
) : ViewModel() {
 
    var deviceModelProvider: () -> String = { android.os.Build.MODEL }

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun authenticate(email: String, password: String, name: String? = null) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                // 1. Try Login
                val loginRequest = UserSyncRequest( // Reusing UserSyncRequest as makeshift LoginRequest
                    email = email,
                    password = password,
                    deviceId = getOrCreateDeviceId(),
                    deviceModel = try { deviceModelProvider() } catch (t: Throwable) { "Unknown" },
                    token = "dummy_fcm_token",
                    timezone = java.util.TimeZone.getDefault().id
                )
                
                // Using authApi.login defined in Step 1151
                val response = authApi.login(loginRequest)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    tokenManager.saveToken(body.accessToken)
                    body.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                    _state.value = AuthState.Success(body)
                } else if (response.code() == 401) { // Unauthorized -> Try Register
                     // Prepare Register Request
                     val registerRequest = com.abstudio.voicenote.data.models.RegisterRequest(
                         email = email,
                         password = password,
                         fullName = name ?: email.split("@")[0], // Default name from email
                         role = "GENERIC"
                     )
                     val regResponse = authApi.register(registerRequest)
                     if (regResponse.isSuccessful && regResponse.body() != null) {
                         val body = regResponse.body()!!
                         tokenManager.saveToken(body.accessToken)
                         body.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                         _state.value = AuthState.Success(body)
                     } else {
                         _state.value = AuthState.Error("Registration failed: ${regResponse.message()}") 
                     }
                } else {
                    _state.value = AuthState.Error("Login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                 _state.value = AuthState.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signInWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            val idToken = googleAuthHelper.signIn(context)
            if (idToken != null) {
                // Mock Success
                val mockUser = UserInfo("google_id", "google@user.com", "Google User", "USER")
                val mockResponse = UserSyncResponse("dummy_google_token", "ref_tok", "Bearer", mockUser, false)
                _state.value = AuthState.Success(mockResponse)
            } else {
                _state.value = AuthState.Error("Google Sign-In failed")
            }
        }
    }

    fun exchangeCodeForToken(code: String) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                // In a real app, this would call an endpoint like authApi.exchangeCode(code)
                // For this project, we simulate the token exchange
                val mockUser = UserInfo("oauth_id", "oauth@user.com", "OAuth User", "USER")
                val mockResponse = UserSyncResponse(
                    accessToken = "oauth_access_token_${UUID.randomUUID()}",
                    refreshToken = "oauth_refresh_token",
                    tokenType = "Bearer",
                    user = mockUser,
                    isNewUser = false
                )
                tokenManager.saveToken(mockResponse.accessToken)
                mockResponse.refreshToken?.let { tokenManager.saveRefreshToken(it) }
                _state.value = AuthState.Success(mockResponse)
            } catch (e: Exception) {
                _state.value = AuthState.Error("OAuth exchange failed: ${e.message}")
            }
        }
    }

    fun updateRole(role: String) {
        viewModelScope.launch {
            try {
                authApi.updateProfile(mapOf("primary_role" to role))
            } catch (_: Exception) {
                // Role update is best-effort during onboarding
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        _state.value = AuthState.Idle
    }

    private fun getOrCreateDeviceId(): String {
        var id = tokenManager.getDeviceId()
        if (id == null) {
            id = UUID.randomUUID().toString()
            tokenManager.saveDeviceId(id)
        }
        return id
    }
}
