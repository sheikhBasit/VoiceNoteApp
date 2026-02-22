package com.abstudio.voicenote.data.local.converters

import androidx.room.TypeConverter
import com.abstudio.voicenote.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    // --- Basic Lists ---
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    // --- Semantic Analysis ---
    @TypeConverter
    fun fromSemanticAnalysis(value: NoteSemanticAnalysis?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSemanticAnalysis(value: String?): NoteSemanticAnalysis? {
        if (value == null) return null
        return gson.fromJson(value, NoteSemanticAnalysis::class.java)
    }

    // --- Metadata ---
    @TypeConverter
    fun fromNoteMetadata(value: NoteMetadata?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNoteMetadata(value: String?): NoteMetadata? {
        if (value == null) return null
        return gson.fromJson(value, NoteMetadata::class.java)
    }

    // --- Business Leads ---
    @TypeConverter
    fun fromBusinessLeadList(value: List<BusinessLead>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toBusinessLeadList(value: String?): List<BusinessLead>? {
        if (value == null) return null
        val type = object : TypeToken<List<BusinessLead>>() {}.type
        return gson.fromJson(value, type)
    }

    // --- Related Notes ---
    @TypeConverter
    fun fromRelatedNoteList(value: List<RelatedNote>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toRelatedNoteList(value: String?): List<RelatedNote>? {
        if (value == null) return null
        val type = object : TypeToken<List<RelatedNote>>() {}.type
        return gson.fromJson(value, type)
    }

    // --- Conflicts ---
    @TypeConverter
    fun fromConflictList(value: List<Conflict>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toConflictList(value: String?): List<Conflict>? {
        if (value == null) return null
        val type = object : TypeToken<List<Conflict>>() {}.type
        return gson.fromJson(value, type)
    }

    // --- Task Fields ---
    @TypeConverter
    fun fromAssignedEntities(value: List<ContactEntity>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toAssignedEntities(value: String?): List<ContactEntity>? {
        if (value == null) return null
        val type = object : TypeToken<List<ContactEntity>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromSuggestedActions(value: SuggestedActions?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toSuggestedActions(value: String?): SuggestedActions? {
        if (value == null) return null
        return gson.fromJson(value, SuggestedActions::class.java)
    }
}
