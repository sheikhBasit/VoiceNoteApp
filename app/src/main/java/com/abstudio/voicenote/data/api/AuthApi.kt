package com.abstudio.voicenote.data.api

import com.abstudio.voicenote.data.models.RegisterRequest
import com.abstudio.voicenote.data.models.UserInfo
import com.abstudio.voicenote.data.models.UserSyncRequest
import com.abstudio.voicenote.data.models.UserSyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/users/login")
    suspend fun login(@Body request: UserSyncRequest): Response<UserSyncResponse>

    @POST("api/v1/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<UserSyncResponse>

    @GET("api/v1/users/me")
    suspend fun getMe(): Response<UserInfo>

    @PATCH("api/v1/users/me")
    suspend fun updateProfile(@Body body: Map<String, String>): Response<UserInfo>
}
