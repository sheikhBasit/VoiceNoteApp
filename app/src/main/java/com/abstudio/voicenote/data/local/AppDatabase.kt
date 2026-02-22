package com.abstudio.voicenote.data.local

import androidx.room.*
import com.abstudio.voicenote.data.local.converters.Converters
import com.abstudio.voicenote.data.local.dao.NoteDao
import com.abstudio.voicenote.data.local.dao.NotificationDao
import com.abstudio.voicenote.data.local.dao.TaskDao
import com.abstudio.voicenote.data.local.entities.NoteEntity
import com.abstudio.voicenote.data.local.entities.TaskEntity
import com.abstudio.voicenote.data.models.NotificationEntity

@Database(
    entities = [NoteEntity::class, TaskEntity::class, NotificationEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun notificationDao(): NotificationDao
}
