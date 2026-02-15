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
        try {
            val response = syncApi.uploadBatch(parts)
            if (response.isSuccessful && response.body() != null) {
                // Mark notes as synced in local DB
                notes.forEach { note ->
                    noteDao.updateNote(note.copy(isSynced = true))
                }
            }
        } catch (e: Exception) {
            // Handle error, retry later
        }
    }

    suspend fun syncPendingNotesFromDb() {
        noteDao.getPendingNotes().collect { pendingNotes ->
            if (pendingNotes.isNotEmpty()) {
                val files = pendingNotes.mapNotNull { it.localAudioPath?.let { path -> File(path) } }
                syncPendingNotes(files, pendingNotes)
            }
        }
    }

    private suspend fun syncPendingNotes(files: List<File>, notes: List<NoteEntity>) {
        if (files.isEmpty()) return

        val parts = files.map { file ->
            val requestFile = file.asRequestBody("audio/mpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files", file.name, requestFile)
        }

        try {
            val response = syncApi.uploadBatch(parts)
            if (response.isSuccessful) {
                notes.forEach { note ->
                    noteDao.updateNote(note.copy(isSynced = true))
                }
            }
        } catch (e: Exception) {
            // Log error
        }
    }
}

data class BatchResponse(
    val status: String,
    val batch_job_id: String,
    val processed_count: Int
)
