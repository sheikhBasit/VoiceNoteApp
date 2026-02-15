package com.example.voicenote.data.api

import com.example.voicenote.data.models.DashboardResponse
import retrofit2.Response
import retrofit2.http.GET

interface DashboardApi {
    @GET("api/v1/notes/dashboard")
    suspend fun getDashboardData(): Response<DashboardResponse>
}
