package com.abstudio.voicenote.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SyncApi {
    @Multipart
    @POST("api/v1/sync/upload-batch")
    suspend fun uploadBatch(
        @Part files: List<MultipartBody.Part>,
        @Part("metadata") metadata: RequestBody? = null
    ): Response<Unit>
}
