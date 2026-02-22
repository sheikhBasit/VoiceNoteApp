package com.abstudio.voicenote.data.repository

import com.abstudio.voicenote.core.util.NetworkResult
import com.abstudio.voicenote.data.api.TasksApi
import com.abstudio.voicenote.data.local.dao.TaskDao
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.models.TaskCreate
import com.abstudio.voicenote.data.models.TaskResponse
import com.abstudio.voicenote.data.models.TaskStatistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val tasksApi: TasksApi,
    private val taskDao: TaskDao
) {

    fun getTasks(): Flow<NetworkResult<List<TaskEntity>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = tasksApi.getTasks()
            if (response.isSuccessful && response.body() != null) {
                val tasks = response.body() ?: run {
                    emit(NetworkResult.Error("Empty response from server"))
                    return@flow
                }
                val entities = tasks.map { it.toEntity(isSynced = true) }
                taskDao.insertTasks(entities)
                emit(NetworkResult.Success(entities))
            } else {
                emit(NetworkResult.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown Error"))
        }
    }

    fun getAllTasksFlow(): Flow<List<TaskEntity>> {
        return taskDao.getAllTasks()
    }
    
    suspend fun getTasksDueToday(): NetworkResult<List<TaskEntity>> {
        return try {
            val response = tasksApi.getTasksDueToday()
            if (response.isSuccessful && response.body() != null) {
                val tasks = response.body() ?: return NetworkResult.Error("Empty response from server")
                val entities = tasks.map { it.toEntity(isSynced = true) }
                // taskDao.insertTasks(entities) // Optional: cache these? Yes.
                NetworkResult.Success(entities)
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch tasks")
        }
    }

    suspend fun getTaskStatistics(): NetworkResult<TaskStatistics> {
        return try {
            val response = tasksApi.getTaskStatistics()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.let { body ->
                    NetworkResult.Success(body)
                } ?: NetworkResult.Error("Empty statistics response")
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Failed to fetch stats")
        }
    }

    suspend fun createTask(request: TaskCreate): NetworkResult<TaskEntity> {
        return try {
            val response = tasksApi.createTask(request)
            if (response.isSuccessful && response.body() != null) {
                val entity = response.body()?.toEntity(isSynced = true) ?: return NetworkResult.Error("Empty response from server")
                taskDao.insertTask(entity)
                NetworkResult.Success(entity)
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Creation Failed")
        }
    }

    suspend fun toggleTaskCompletion(taskId: String, isDone: Boolean): NetworkResult<TaskEntity> {
        return try {
            val response = tasksApi.toggleTaskCompletion(taskId, mapOf("is_done" to isDone))
            if (response.isSuccessful && response.body() != null) {
                val entity = response.body()?.toEntity(isSynced = true) ?: return NetworkResult.Error("Empty response from server")
                taskDao.updateTask(entity)
                NetworkResult.Success(entity)
            } else {
                NetworkResult.Error(response.message())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Toggle Failed")
        }
    }

    private fun TaskResponse.toEntity(isSynced: Boolean): TaskEntity {
        return TaskEntity(
            id = this.id,
            noteId = this.noteId,
            title = this.title,
            description = this.description,
            priority = this.priority,
            status = this.status,
            isDone = this.isDone,
            deadline = this.deadline,
            teamId = this.teamId,
            assignedEntities = this.assignedEntities,
            suggestedActions = this.suggestedActions,
            isSynced = isSynced,
            isDeleted = this.isDeleted,
            createdAt = this.createdAt,
            updatedAt = this.updatedAt,
            deletedAt = this.deletedAt,
            communicationType = this.communicationType,
            isActionApproved = this.isActionApproved
        )
    }
}
