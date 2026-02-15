package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class UserSyncRequest(
    @SerializedName("email") val email: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_name") val deviceName: String
)

data class UserSyncResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: UserInfo
)

data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("primary_role") val primaryRole: String?,
    @SerializedName("is_verified") val isVerified: Boolean
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("role") val role: String? = "GENERIC"
)
