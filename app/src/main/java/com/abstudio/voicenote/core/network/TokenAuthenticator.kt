package com.abstudio.voicenote.core.network

import com.abstudio.voicenote.data.local.TokenManager
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

data class RefreshTokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("token_type") val tokenType: String
)

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager
) : Authenticator {

    private val gson = Gson()

    @Volatile
    private var isRefreshing = false

    override fun authenticate(route: Route?, response: Response): Request? {
        // Don't retry if the refresh endpoint itself failed
        if (response.request.url.encodedPath.contains("/users/refresh")) {
            return null
        }

        // Don't retry more than once for the same request
        if (responseCount(response) > 1) {
            return null
        }

        synchronized(this) {
            // Check if another thread already refreshed the token
            val currentToken = tokenManager.getToken()
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            if (currentToken != null && currentToken != requestToken) {
                // Token was already refreshed by another thread, retry with new token
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken() ?: run {
                clearAndLogout()
                return null
            }

            return try {
                isRefreshing = true
                val newTokens = performRefresh(refreshToken, response.request.url.toString())
                if (newTokens != null) {
                    tokenManager.saveToken(newTokens.accessToken)
                    tokenManager.saveRefreshToken(newTokens.refreshToken)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${newTokens.accessToken}")
                        .build()
                } else {
                    clearAndLogout()
                    null
                }
            } catch (e: Exception) {
                clearAndLogout()
                null
            } finally {
                isRefreshing = false
            }
        }
    }

    private fun performRefresh(refreshToken: String, originalUrl: String): RefreshTokenResponse? {
        // Extract base URL from the original request
        val baseUrl = originalUrl.substringBefore("/api/v1/") + "/api/v1/"
        val url = "${baseUrl}users/refresh"

        val body = gson.toJson(RefreshTokenRequest(refreshToken))
            .toRequestBody("application/json".toMediaType())

        // Use a bare OkHttpClient to avoid interceptor loops
        val client = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Content-Type", "application/json")
            .build()

        val response = client.newCall(request).execute()
        return if (response.isSuccessful) {
            response.body?.string()?.let { responseBody ->
                gson.fromJson(responseBody, RefreshTokenResponse::class.java)
            }
        } else {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun clearAndLogout() {
        tokenManager.clear()
    }
}
