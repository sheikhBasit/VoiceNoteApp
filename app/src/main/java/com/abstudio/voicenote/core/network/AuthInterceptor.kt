package com.abstudio.voicenote.core.network

import com.abstudio.voicenote.data.local.SettingsDataStore
import com.abstudio.voicenote.data.local.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val settingsDataStore: SettingsDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        // 1. Authorization: Bearer Token
        val token = tokenManager.getToken()
        if (token != null) {
            builder.header("Authorization", "Bearer $token")
        }

        // 2. Backend Helper: TimeZone
        try {
            val tz = TimeZone.getDefault().id
            builder.header("X-TimeZone", tz)
        } catch (e: Exception) {
            builder.header("X-TimeZone", "UTC")
        }

        // 3. Backend Helper: Device ID
        val deviceId = tokenManager.getDeviceId()
        if (deviceId != null) {
            builder.header("X-Device-ID", deviceId)
        }
        
        // 4. Backend Helper: GPS Coords
        val locationEnabled = runBlocking { 
            settingsDataStore.shareLocation.first()
        }
        if (locationEnabled) {
            // Mocking high-precision coords for backend cost allocation logic
            builder.header("X-GPS-Coords", "34.0522,-118.2437") 
        }

        return chain.proceed(builder.build())
    }
}
