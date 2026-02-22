package com.abstudio.voicenote.data.api

import com.abstudio.voicenote.data.models.PlanResponse
import com.abstudio.voicenote.data.models.VerifyPurchaseRequest
import com.abstudio.voicenote.data.models.VerifyPurchaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BillingApi {
    @GET("api/v1/billing/plans")
    suspend fun getPlans(): Response<List<PlanResponse>>

    @POST("api/v1/billing/verify-purchase")
    suspend fun verifyPurchase(@Body body: VerifyPurchaseRequest): Response<VerifyPurchaseResponse>
}
