package com.abstudio.voicenote.data.models

data class AIKeyPoint(
    val type: AIKeyPointType,
    val text: String
)

enum class AIKeyPointType(val label: String, val color: Long) {
    ACTION("Action Item", 0xFFEF4444),
    DECISION("Key Decision", 0xFF10B981),
    INSIGHT("Insight", 0xFFF59E0B)
}
