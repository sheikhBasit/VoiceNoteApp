package com.abstudio.voicenote.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.abstudio.voicenote.data.local.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AICustomizationUiState(
    val role: AIRole = AIRole.DEVELOPER,
    val temperature: Float = 0.7f,
    val style: CommStyle = CommStyle.DETAILED,
    val shareLocation: Boolean = false
)

@HiltViewModel
class AICustomizationViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val uiState: StateFlow<AICustomizationUiState> = combine(
        settingsDataStore.aiRole,
        settingsDataStore.aiTemperature,
        settingsDataStore.aiCommunicationStyle,
        settingsDataStore.shareLocation
    ) { role, temp, style, location ->
        AICustomizationUiState(
            role = try { AIRole.valueOf(role) } catch(e: Exception) { AIRole.DEVELOPER },
            temperature = temp,
            style = try { CommStyle.valueOf(style) } catch(e: Exception) { CommStyle.DETAILED },
            shareLocation = location
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AICustomizationUiState()
    )

    fun saveSettings(role: AIRole, temperature: Float, style: CommStyle) {
        viewModelScope.launch {
            settingsDataStore.saveAiSettings(role.name, temperature, style.name)
        }
    }

    fun toggleLocation(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.saveLocationSetting(enabled)
        }
    }
}
