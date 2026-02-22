package com.abstudio.voicenote.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = context.dataStore

    companion object {
        val AI_ROLE = stringPreferencesKey("ai_role")
        val AI_TEMPERATURE = floatPreferencesKey("ai_temperature")
        val AI_COMMUNICATION_STYLE = stringPreferencesKey("ai_communication_style")
        val SHARE_LOCATION = booleanPreferencesKey("share_location")
    }

    val aiRole: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AI_ROLE] ?: "Professional Assistant"
    }

    val aiTemperature: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[AI_TEMPERATURE] ?: 0.7f
    }

    val aiCommunicationStyle: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[AI_COMMUNICATION_STYLE] ?: "Concise"
    }

    val shareLocation: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SHARE_LOCATION] ?: false
    }

    suspend fun saveAiSettings(role: String, temperature: Float, style: String) {
        context.dataStore.edit { preferences ->
            preferences[AI_ROLE] = role
            preferences[AI_TEMPERATURE] = temperature
            preferences[AI_COMMUNICATION_STYLE] = style
        }
    }

    suspend fun saveLocationSetting(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHARE_LOCATION] = enabled
        }
    }
}
