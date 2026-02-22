package com.abstudio.voicenote.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.abstudio.voicenote.data.local.entities.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%') OR (summary LIKE '%' || :query || '%') OR (transcript LIKE '%' || :query || '%') AND isDeleted = 0")
    suspend fun searchNotesLocal(query: String): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<NoteEntity>)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: String)
    
    @Query("DELETE FROM notes")
    suspend fun clearNotes()

    @Query("SELECT * FROM notes WHERE isSynced = 0")
    suspend fun getPendingSyncNotes(): List<NoteEntity>
}
