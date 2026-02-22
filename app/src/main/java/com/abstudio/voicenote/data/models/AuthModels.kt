package com.abstudio.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class UserSyncRequest(
    @SerializedName("email") val email: String,
    @SerializedName("device_id") val deviceId: String,
    @SerializedName("device_model") val deviceModel: String,
    @SerializedName("token") val token: String, // Biometric/Device token
    @SerializedName("password") val password: String? = null,
    @SerializedName("timezone") val timezone: String? = "UTC"
)

data class UserSyncResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("user") val user: UserInfo,
    @SerializedName("is_new_user") val isNewUser: Boolean = false
)

data class UserInfo(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val fullName: String?, // Backend sends 'name'
    @SerializedName("primary_role") val primaryRole: String?,
    @SerializedName("is_deleted") val isDeleted: Boolean = false
)

data class RegisterRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("role") val role: String? = "GENERIC"
)
