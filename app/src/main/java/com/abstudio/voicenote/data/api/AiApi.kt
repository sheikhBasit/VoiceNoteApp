package com.abstudio.voicenote.data.api

import com.abstudio.voicenote.data.models.AiAskRequest
import com.abstudio.voicenote.data.models.AiAskResponse
import com.abstudio.voicenote.data.models.AiSearchRequest
import com.abstudio.voicenote.data.models.NoteDetailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {
    @POST("api/v1/ai/ask")
    suspend fun askAi(@Body request: AiAskRequest): Response<AiAskResponse>

    @POST("api/v1/ai/search")
    suspend fun semanticSearch(@Body request: AiSearchRequest): Response<List<NoteDetailResponse>>
}
