package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("due_date") val dueDate: String?,
    @SerializedName("priority") val priority: String, // "CRITICAL", "HIGH", "MEDIUM", "LOW"
    @SerializedName("status") val status: String,
    @SerializedName("assigned_to") val assignedTo: String?,
    @SerializedName("suggested_action") val suggestedAction: SuggestedAction?
)

data class SuggestedAction(
    @SerializedName("type") val type: String, // "WHATSAPP", "EMAIL", "CALENDAR", "MAPS"
    @SerializedName("target") val target: String, // Phone number, email, or intent URI
    @SerializedName("text") val text: String
)

data class TaskCenterResponse(
    @SerializedName("attention_needed") val criticalTasks: List<TaskResponse>,
    @SerializedName("priority_queue") val priorityQueue: List<TaskResponse>
)
