package com.abstudio.voicenote.data.models

import com.google.gson.annotations.SerializedName

data class SearchQuery(
    @SerializedName("query") val query: String,
    @SerializedName("limit") val limit: Int = 10,
    @SerializedName("offset") val offset: Int = 0
)
