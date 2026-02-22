package com.abstudio.voicenote.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.abstudio.voicenote.data.local.entities.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isDeleted = 0 ORDER BY priority DESC, deadline ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>
    
    @Query("SELECT * FROM tasks WHERE noteId = :noteId AND isDeleted = 0 ORDER BY priority DESC")
    fun getTasksForNote(noteId: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)
    
    @Query("DELETE FROM tasks")
    suspend fun clearTasks()
}
