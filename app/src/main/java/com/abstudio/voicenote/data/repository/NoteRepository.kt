package com.abstudio.voicenote.data.repository

import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.api.NotesApi
import com.abstudio.voicenote.data.local.dao.NoteDao
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.models.NoteDetailResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository @Inject constructor(
    private val notesApi: NotesApi,
    private val noteDao: NoteDao
) {

    // Fetch notes from API and cache locally
    fun getNotes(skip: Int = 0, limit: Int = 50): Flow<NetworkResult<List<NoteEntity>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = notesApi.getNotes(skip, limit)
            if (response.isSuccessful && response.body() != null) {
                val notes = response.body() ?: run {
                    emit(NetworkResult.Error("Empty response from server"))
                    return@flow
                }
                val entities = notes.map { it.toEntity(isSynced = true) }
                if (skip == 0) {
                     // For simplicity, if we pull fresh page 0, we might want to clear or just upsert.
                     // Upsert is safer.
                }
                noteDao.insertNotes(entities)
                emit(NetworkResult.Success(entities))
            } else {
                emit(NetworkResult.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    suspend fun searchNotes(query: String): NetworkResult<List<NoteEntity>> {
          return try {
               val response = notesApi.searchNotes(com.abstudio.voicenote.data.models.SearchQuery(query))
               if (response.isSuccessful && response.body() != null) {
                   val notes = response.body()!!
                   val entities = notes.map { it.toEntity(isSynced = true) }
                   NetworkResult.Success(entities)
               } else {
                    // FALLBACK: Local Search
                    val localResults = noteDao.searchNotesLocal(query)
                    NetworkResult.Success(localResults)
               }
          } catch(e: Exception) {
               val localResults = noteDao.searchNotesLocal(query)
               if (localResults.isNotEmpty()) NetworkResult.Success(localResults)
               else NetworkResult.Error(e.message ?: "Search Failed")
          }
    }

    // Get local flow for UI observation
    fun getAllNotesFlow(): Flow<List<NoteEntity>> {
        return noteDao.getAllNotes()
    }

    suspend fun getNote(id: String): NetworkResult<NoteEntity> {
        // 1. Check local
        val localNote = noteDao.getNoteById(id)
        
        try {
             val response = notesApi.getNoteDetail(id)
             if (response.isSuccessful && response.body() != null) {
                 val detail = response.body() ?: run {
                     if (localNote != null) return NetworkResult.Success(localNote)
                     return NetworkResult.Error("Empty response from server")
                 }
                 // Map to entity
                 val entity = detail.toEntity(isSynced = true)
                 noteDao.insertNote(entity)
                 return NetworkResult.Success(entity)
             } else {
                 if (localNote != null) return NetworkResult.Success(localNote) // Fallback
                 return NetworkResult.Error(response.message())
             }
        } catch (e: Exception) {
             if (localNote != null) return NetworkResult.Success(localNote) // Fallback
             return NetworkResult.Error(e.message ?: "Fetch failed")
        }
    }

    fun getNoteById(id: String): Flow<NetworkResult<NoteDetailResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = notesApi.getNoteDetail(id)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { body ->
                    emit(NetworkResult.Success(body))
                } ?: emit(NetworkResult.Error("Empty response"))
            } else {
                emit(NetworkResult.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Fetch failed"))
        }
    }

    suspend fun processNote(
        file: MultipartBody.Part?,
        mode: RequestBody?,
        sttModel: RequestBody?,
        languages: RequestBody?
    ): NetworkResult<NoteDetailResponse> {
        return try {
            val response = notesApi.processNote(file, mode, sttModel, languages)
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { body ->
                    NetworkResult.Success(body)
                } ?: NetworkResult.Error("Empty response from server")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Upload Failed")
        }
    }

    suspend fun getDashboardMetrics(): NetworkResult<com.abstudio.voicenote.data.models.DashboardResponse> {
        return try {
            val response = notesApi.getDashboardMetrics()
            if (response.isSuccessful && response.body() != null) {
                NetworkResult.Success(response.body()!!)
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Dashboard fetch failed")
        }
    }

    /**
     * Eagerly pre-fetches and caches multiple notes to ensure instant transition.
     */
    suspend fun warmCache(noteIds: List<String>) {
        noteIds.take(5).forEach { id ->
            launchWarmTask(id)
        }
    }

    private suspend fun launchWarmTask(id: String) {
        try {
            // Only fetch if not already in local DB (optional optimization)
            // For now, refresh cache
            val response = notesApi.getNoteDetail(id)
            if (response.isSuccessful && response.body() != null) {
                noteDao.insertNote(response.body()!!.toEntity(isSynced = true))
            }
        } catch (e: Exception) {
            // Silent fail for warming
        }
    }

    suspend fun uploadVoiceNote(file: java.io.File): NetworkResult<NoteDetailResponse> {
        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val mode = "GENERIC".toRequestBody("text/plain".toMediaTypeOrNull())
        
        return processNote(body, mode, null, null)
    }

    // Mapper extension (simplified for now)
    private fun NoteDetailResponse.toEntity(isSynced: Boolean): NoteEntity {
        return NoteEntity(
            id = this.id,
            title = this.title,
            summary = this.summary,
            transcript = this.transcript,
            status = "DONE", // API returns processed notes usually
            timestamp = this.timestamp,
            audioPath = null, // Store if needed
            localAudioPath = null,
            isSynced = isSynced,
            metadata = this.metadata,
            semanticAnalysis = this.semanticAnalysis,
            businessLeads = this.businessLeads,
            relatedNotes = this.relatedNotes,
            conflicts = this.conflicts
        )
    }
}
