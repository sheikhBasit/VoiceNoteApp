package com.example.voicenote.data.api

import com.example.voicenote.data.models.TaskCenterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface TasksApi {
    @GET("api/v1/tasks/center")
    suspend fun getTaskCenter(): Response<TaskCenterResponse>

    @PATCH("api/v1/tasks/{id}/status")
    suspend fun updateTaskStatus(@Path("id") id: String, status: String): Response<Unit>
}
