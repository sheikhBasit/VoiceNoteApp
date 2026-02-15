package com.example.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("stats") val stats: DashboardStats,
    @SerializedName("recent_notes") val recentNotes: List<NoteSummary>,
    @SerializedName("ai_insights") val aiInsights: List<AIInsight>
)

data class DashboardStats(
    @SerializedName("total_notes") val totalNotes: Int,
    @SerializedName("processed_notes") val processedNotes: Int,
    @SerializedName("total_tasks") val totalTasks: Int,
    @SerializedName("meeting_roi") val meetingRoi: String?, // e.g. "85%"
    @SerializedName("productivity_velocity") val velocity: String?, // e.g. "+12%"
    @SerializedName("decision_heatmap") val heatmap: List<HeatmapData>?
)

data class HeatmapData(
    @SerializedName("day") val day: String,
    @SerializedName("intensity") val intensity: Float
)

data class NoteSummary(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("status") val status: String
)

data class AIInsight(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type") val type: String // "PRIORITY", "SUGGESTION", etc.
)
