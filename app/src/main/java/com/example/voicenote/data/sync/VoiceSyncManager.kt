package com.example.voicenote.data.sync

import com.example.voicenote.data.api.SyncApi
import com.example.voicenote.data.local.NoteDao
import com.example.voicenote.data.local.NoteEntity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class VoiceSyncManager(
    private val syncApi: SyncApi,
    private val noteDao: NoteDao
) {
    suspend fun syncPendingNotes(files: List<File>) {
        if (files.isEmpty()) return

        val parts = files.map { file ->
            val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", file.name, requestFile)
        }

        try {
            val response = syncApi.uploadBatch(parts)
            if (response.isSuccessful) {
                // Mark notes as synced in local DB or delete temporary files
            }
        } catch (e: Exception) {
            // Handle error, retry later
        }
    }
}
