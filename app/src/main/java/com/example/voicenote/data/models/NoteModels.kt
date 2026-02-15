package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class NoteDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("transcript") val transcript: String, // Backend returns formatted string
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("tasks") val tasks: List<NoteTask>?,
    @SerializedName("semantic_analysis") val semanticAnalysis: NoteSemanticAnalysis?,
    @SerializedName("conflicts") val conflicts: List<Conflict>?,
    @SerializedName("metadata") val metadata: NoteMetadata?
)

data class NoteTask(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String,
    @SerializedName("is_done") val isDone: Boolean,
    @SerializedName("priority") val priority: String
)

data class NoteSemanticAnalysis(
    @SerializedName("sentiment") val sentiment: String,
    @SerializedName("key_insights") val keyInsights: List<String>,
    @SerializedName("logical_patterns") val logicalPatterns: List<String>,
    @SerializedName("emotional_tone") val emotionalTone: String
)

data class TranscriptSegment(
    @SerializedName("speaker") val speaker: String?, // "Speaker 1", "Speaker 2"
    @SerializedName("text") val text: String,
    @SerializedName("start") val start: Float,
    @SerializedName("end") val end: Float
)

data class KeyPoints(
    @SerializedName("action_items") val actionItems: List<String>,
    @SerializedName("decisions") val decisions: List<String>,
    @SerializedName("insights") val insights: List<String>
)

data class Conflict(
    @SerializedName("id") val id: String,
    @SerializedName("source_note_title") val sourceNoteTitle: String,
    @SerializedName("description") val description: String
)

data class NoteMetadata(
    @SerializedName("duration") val duration: Float,
    @SerializedName("sentiment") val sentiment: String,
    @SerializedName("tone") val tone: String
)
