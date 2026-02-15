package com.example.voicenote.data.api

import com.example.voicenote.data.models.NoteDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface NotesApi {
    @GET("api/v1/notes/{id}")
    suspend fun getNoteDetail(@Path("id") id: String): Response<NoteDetailResponse>
}
