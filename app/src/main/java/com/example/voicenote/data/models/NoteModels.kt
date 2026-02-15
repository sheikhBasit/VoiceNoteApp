package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class NoteDetailResponse(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("summary") val summary: String,
    @SerializedName("transcript") val transcript: List<TranscriptSegment>,
    @SerializedName("key_points") val keyPoints: KeyPoints,
    @SerializedName("conflicts") val conflicts: List<Conflict>?,
    @SerializedName("metadata") val metadata: NoteMetadata
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
