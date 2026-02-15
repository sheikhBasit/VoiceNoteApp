package com.example.voicenote.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voicenote.data.api.DashboardApi
import com.example.voicenote.data.models.DashboardResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DashboardState {
    object Loading : DashboardState()
    data class Success(val data: DashboardResponse) : DashboardState()
    data class Error(val message: String) : DashboardState()
}

class DashboardViewModel(
    private val dashboardApi: DashboardApi,
    private val sseManager: com.example.voicenote.core.network.SSEManager,
    private val okHttpClient: okhttp3.OkHttpClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardState>(DashboardState.Loading)
    val uiState: StateFlow<DashboardState> = _uiState

    init {
        loadDashboard()
        startSseConnection()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = DashboardState.Loading
            try {
                val response = dashboardApi.getDashboardData()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = DashboardState.Success(response.body()!!)
                } else {
                    _uiState.value = DashboardState.Error("Failed to load dashboard")
                }
            } catch (e: Exception) {
                _uiState.value = DashboardState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun startSseConnection() {
        sseManager.connect(
            "http://4.240.96.60:8000/sse/events",
            object : com.example.voicenote.core.network.SSEManager.SSEListener {
                override fun onOpen() {
                    println("SSE Connected")
                }

                override fun onEvent(type: String?, data: String) {
                    // Update dashboard when a note is processed or generic update
                    if (type == "NOTE_PROCESSED" || type == "DASHBOARD_UPDATE") {
                        loadDashboard()
                    }
                }

                override fun onClosed() {
                    println("SSE Closed")
                }

                override fun onError(t: Throwable?) {
                    println("SSE Error: ${t?.message}")
                }
            })
    }

    override fun onCleared() {
        super.onCleared()
        sseManager.disconnect()
    }
}
