package com.abstudio.voicenote.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abstudio.voicenote.data.models.ContactEntity
import com.abstudio.voicenote.data.models.SuggestedActions

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val noteId: String?,
    val title: String?,
    val description: String,
    val priority: String, // HIGH, MEDIUM, LOW
    val status: String, // TODO, DONE
    val isDone: Boolean,
    val deadline: Long?,
    val teamId: String?,
    
    // Rich Data (via TypeConverters)
    val assignedEntities: List<ContactEntity>? = null,
    val suggestedActions: SuggestedActions? = null,
    
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,
    val deletedAt: Long? = null,
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
    val communicationType: String? = null,
    val isActionApproved: Boolean = false
)
