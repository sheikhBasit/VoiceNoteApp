package com.example.voicenote.data.api

import com.example.voicenote.data.models.TaskCenterResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface TasksApi {
    @GET("api/v1/tasks")
    suspend fun getTasks(): Response<List<TaskResponse>>

    @GET("api/v1/tasks/center")
    suspend fun getTaskCenter(): Response<TaskCenterResponse>

    @PATCH("api/v1/tasks/{id}/complete")
    suspend fun toggleTaskCompletion(@Path("id") id: String, @Body body: Map<String, Boolean>): Response<TaskResponse>

    @PATCH("api/v1/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body updates: Map<String, Any>): Response<TaskResponse>

    @DELETE("api/v1/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>
}
