package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String,
    @SerializedName("deadline") val deadline: Long?,
    @SerializedName("priority") val priority: String,
    @SerializedName("status") val status: String,
    @SerializedName("is_done") val isDone: Boolean,
    @SerializedName("assigned_entities") val assignedEntities: List<Map<String, String>>?,
    @SerializedName("actions") val actions: Map<String, String>? // Deep link dictionary
)

data class TaskCenterResponse(
    @SerializedName("attention_needed") val criticalTasks: List<TaskResponse>,
    @SerializedName("priority_queue") val priorityQueue: List<TaskResponse>
)
