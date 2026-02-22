package com.abstudio.voicenote.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.abstudio.voicenote.data.models.*

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val transcript: String,
    val status: String, // PENDING, DONE
    val timestamp: Long,
    val audioPath: String?, // Remote URL or Key
    val localAudioPath: String?, // Local file path
    val isSynced: Boolean = false,
    
    // Rich Data (via TypeConverters)
    val semanticAnalysis: NoteSemanticAnalysis? = null,
    val metadata: NoteMetadata? = null,
    val businessLeads: List<BusinessLead>? = null,
    val relatedNotes: List<RelatedNote>? = null,
    val conflicts: List<Conflict>? = null,
    val isDeleted: Boolean = false
)
