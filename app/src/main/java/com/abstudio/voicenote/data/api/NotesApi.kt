package com.abstudio.voicenote.data.api

import com.abstudio.voicenote.data.models.DashboardResponse
import com.abstudio.voicenote.data.models.NoteDetailResponse
import com.abstudio.voicenote.data.models.SearchQuery
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("api/v1/notes/process")
    suspend fun processNote(
        @Part file: MultipartBody.Part?,
        @Part("mode") mode: RequestBody?,
        @Part("stt_model") sttModel: RequestBody?,
        @Part("languages") languages: RequestBody?
    ): Response<NoteDetailResponse>

    @POST("api/v1/notes/{id}/ask")
    suspend fun askAboutNote(
        @Path("id") id: String,
        @Body body: Map<String, String>
    ): Response<Map<String, String>>

    @POST("api/v1/notes/search")
    suspend fun searchNotes(@Body query: SearchQuery): Response<List<NoteDetailResponse>>

    @GET("api/v1/notes/dashboard")
    suspend fun getDashboardMetrics(): Response<DashboardResponse>
}

data class NoteCreate(
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("transcript") val transcript: String,
    @SerializedName("user_id") val userId: String
)
