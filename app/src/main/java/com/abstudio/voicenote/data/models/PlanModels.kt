package com.abstudio.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class PlanResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("monthly_credits") val monthlyCredits: Int,
    @SerializedName("price_per_minute") val pricePerMinute: Int,
    @SerializedName("monthly_note_limit") val monthlyNoteLimit: Int,
    @SerializedName("monthly_task_limit") val monthlyTaskLimit: Int,
    @SerializedName("max_storage_mb") val maxStorageMb: Int,
    @SerializedName("features") val features: Map<String, Boolean>,
    @SerializedName("google_play_product_id") val googlePlayProductId: String?,
    @SerializedName("is_active") val isActive: Boolean
)

data class VerifyPurchaseRequest(
    @SerializedName("purchase_token") val purchaseToken: String,
    @SerializedName("product_id") val productId: String
)

data class VerifyPurchaseResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("new_tier") val newTier: String?,
    @SerializedName("message") val message: String
)
