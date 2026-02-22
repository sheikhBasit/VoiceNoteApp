package com.abstudio.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class AiAskRequest(
    @SerializedName("question") val question: String
)

data class AiAskResponse(
    @SerializedName("answer") val answer: String
)

data class AiSearchRequest(
    @SerializedName("query") val query: String,
    @SerializedName("limit") val limit: Int = 5
)
