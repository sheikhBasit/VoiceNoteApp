package com.example.voicenote.data.api

import com.example.voicenote.data.models.RegisterRequest
import com.example.voicenote.data.models.UserSyncRequest
import com.example.voicenote.data.models.UserSyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<Unit>

    @POST("api/v1/users/sync")
    suspend fun syncDevice(@Body request: UserSyncRequest): Response<UserSyncResponse>
}
