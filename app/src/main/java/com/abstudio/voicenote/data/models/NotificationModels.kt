package com.abstudio.voicenote.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "AI_INSIGHT", "WORKSPACE_INVITE", "DEADLINE"
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("timestamp") val timestamp: Long,
    @ColumnInfo(name = "is_read")
    @SerializedName("is_read") var isRead: Boolean = false,
    @SerializedName("action_label") val actionLabel: String? = null
)
