package com.example.voicenote.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

import com.example.voicenote.data.models.NotificationEntity

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val transcript: String,
    val status: String,
    val timestamp: Long,
    val audioPath: String,
    val localAudioPath: String?,
    val isSynced: Boolean = false
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: String): NoteEntity?
    
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNote(id: String)
}

@Database(entities = [NoteEntity::class, NotificationEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun notificationDao(): NotificationDao
}
