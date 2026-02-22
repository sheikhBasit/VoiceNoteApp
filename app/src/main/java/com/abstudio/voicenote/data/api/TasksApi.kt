package com.abstudio.voicenote.data.api

import com.abstudio.voicenote.data.models.TaskCreate
import com.abstudio.voicenote.data.models.TaskResponse
import com.abstudio.voicenote.data.models.TaskStatistics
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface TasksApi {
    @GET("api/v1/tasks")
    suspend fun getTasks(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0
    ): Response<List<TaskResponse>>

    @POST("api/v1/tasks")
    suspend fun createTask(@Body request: TaskCreate): Response<TaskResponse>

    @GET("api/v1/tasks/due-today")
    suspend fun getTasksDueToday(): Response<List<TaskResponse>>

    @GET("api/v1/tasks/overdue")
    suspend fun getOverdueTasks(): Response<List<TaskResponse>>

    @GET("api/v1/tasks/stats")
    suspend fun getTaskStatistics(): Response<TaskStatistics>

    @POST("api/v1/tasks/{id}/lock")
    suspend fun lockTask(@Path("id") id: String): Response<Map<String, Any>>

    @DELETE("api/v1/tasks/{id}/lock")
    suspend fun unlockTask(@Path("id") id: String): Response<Map<String, Any>>

    @Multipart
    @POST("api/v1/tasks/{id}/multimedia")
    suspend fun uploadMultimedia(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<Map<String, Any>>

    @PATCH("api/v1/tasks/{id}/complete")
    suspend fun toggleTaskCompletion(@Path("id") id: String, @Body body: Map<String, Boolean>): Response<TaskResponse>

    @PATCH("api/v1/tasks/{id}")
    suspend fun updateTask(@Path("id") id: String, @Body updates: Map<String, Any>): Response<TaskResponse>

    @DELETE("api/v1/tasks/{id}")
    suspend fun deleteTask(@Path("id") id: String): Response<Unit>
}
