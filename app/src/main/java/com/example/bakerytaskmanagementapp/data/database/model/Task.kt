package com.example.bakerytaskmanagementapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

object TaskStatusType {
    const val PENDING = 0
    const val COMPLETED = 1
    const val DELETED = 2
}

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val description: String?,
    val status: Int,
    @ColumnInfo(name = "is_priority") val isPriority: Boolean,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    @ColumnInfo(name = "date_deadline") val dateDeadline: Long?,
    @ColumnInfo(name = "date_last_updated") val dateLastUpdated: Long,
    @ColumnInfo(name = "date_deleted") val dateDeleted: Long?,
    @ColumnInfo(name = "date_completed") val dateCompleted: Long?
)
