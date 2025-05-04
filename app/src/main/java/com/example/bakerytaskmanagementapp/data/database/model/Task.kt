package com.example.bakerytaskmanagementapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

object TaskStatusType {
    const val PENDING = 0
    const val COMPLETED = 1
    const val DELETED = 2
}

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val status: Int = 0,
    @ColumnInfo(name = "is_priority") val isPriority: Boolean = false,
    @ColumnInfo(name = "date_created") val dateCreated: Date = Date(),
    @ColumnInfo(name = "date_deadline") val dateDeadline: Date? = null,
    @ColumnInfo(name = "date_last_updated") val dateLastUpdated: Date = Date(),
    @ColumnInfo(name = "date_deleted") val dateDeleted: Date? = null,
    @ColumnInfo(name = "date_completed") val dateCompleted: Date? = null
)
