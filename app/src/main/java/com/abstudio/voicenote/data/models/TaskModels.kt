package com.abstudio.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class TaskResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String,
    @SerializedName("deadline") val deadline: Long?, // Timestamp in milliseconds or null
    @SerializedName("priority") val priority: String, // HIGH, MEDIUM, LOW
    @SerializedName("status") val status: String, // TODO, IN_PROGRESS, DONE, BLOCKED
    @SerializedName("is_done") val isDone: Boolean,
    @SerializedName("assigned_entities") val assignedEntities: List<ContactEntity>?,
    @SerializedName("suggested_actions") val suggestedActions: SuggestedActions?,
    @SerializedName("note_id") val noteId: String?,
    @SerializedName("team_id") val teamId: String?,
    @SerializedName("is_deleted") val isDeleted: Boolean,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("updated_at") val updatedAt: Long?,
    @SerializedName("deleted_at") val deletedAt: Long?,
    @SerializedName("communication_type") val communicationType: String?,
    @SerializedName("is_action_approved") val isActionApproved: Boolean
)

data class TaskCreate(
    @SerializedName("note_id") val noteId: String? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("description") val description: String,
    @SerializedName("priority") val priority: String = "MEDIUM",
    @SerializedName("deadline") val deadline: Long? = null,
    @SerializedName("assigned_entities") val assignedEntities: List<ContactEntity> = emptyList(),
    @SerializedName("team_id") val teamId: String? = null,
    @SerializedName("image_uris") val imageUris: List<String> = emptyList(),
    @SerializedName("document_uris") val documentUris: List<String> = emptyList(),
    @SerializedName("external_links") val externalLinks: List<LinkEntity> = emptyList(),
    @SerializedName("communication_type") val communicationType: String? = null,
    @SerializedName("is_action_approved") val isActionApproved: Boolean = false
)

data class TaskStatistics(
    @SerializedName("total_tasks") val totalTasks: Int,
    @SerializedName("completed_tasks") val completedTasks: Int,
    @SerializedName("pending_tasks") val pendingTasks: Int,
    @SerializedName("by_priority") val byPriority: Map<String, Int>,
    @SerializedName("by_status") val byStatus: Map<String, Int>,
    @SerializedName("completion_rate") val completionRate: Float
)

data class SuggestedActions(
    @SerializedName("google_search") val googleSearch: Map<String, String>?,
    @SerializedName("map") val map: Map<String, String>?,
    @SerializedName("email") val email: Map<String, String>?,
    @SerializedName("whatsapp") val whatsapp: Map<String, String>?,
    @SerializedName("ai_prompt") val aiPrompt: Map<String, String>?
)

data class ContactEntity(
    @SerializedName("name") val name: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?
)

data class LinkEntity(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String
)
