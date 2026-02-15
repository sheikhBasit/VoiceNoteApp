package com.example.voicenote.data.api

import com.example.voicenote.data.models.NoteDetailResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesApi {
    @GET("api/v1/notes")
    suspend fun getNotes(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 50
    ): Response<List<NoteDetailResponse>>

    @GET("api/v1/notes/{id}")
    suspend fun getNoteDetail(@Path("id") id: String): Response<NoteDetailResponse>

    @POST("api/v1/notes")
    suspend fun createNote(@Body request: NoteCreate): Response<NoteDetailResponse>

    @PATCH("api/v1/notes/{id}")
    suspend fun updateNote(@Path("id") id: String, @Body request: Map<String, Any>): Response<NoteDetailResponse>

    @DELETE("api/v1/notes/{id}")
    suspend fun deleteNote(@Path("id") id: String): Response<Unit>
}

data class NoteCreate(
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("transcript") val transcript: String,
    @SerializedName("user_id") val userId: String
)
