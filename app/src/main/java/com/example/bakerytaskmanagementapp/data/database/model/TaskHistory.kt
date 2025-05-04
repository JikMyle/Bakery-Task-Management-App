package com.example.bakerytaskmanagementapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "task_history",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class TaskHistory(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "task_id") val taskId: Int,
    @ColumnInfo(name = "action_done") val actionDone: Int,
    @ColumnInfo(name = "date_action_done") val dateActionDone: Date,
)

object TaskHistoryAction {
    const val CREATED = 0
    const val UPDATED = 1
    const val COMPLETED = 2
    const val DELETED = 3
}
