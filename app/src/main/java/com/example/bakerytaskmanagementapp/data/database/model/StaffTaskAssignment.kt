package com.example.bakerytaskmanagementapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "staff_task_assignment",
    primaryKeys = ["task_id", "staff_id"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = Staff::class,
            parentColumns = ["id"],
            childColumns = ["staff_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
)
data class StaffTaskAssignment(
    @ColumnInfo(name = "task_id") val taskId: Int,
    @ColumnInfo(name = "staff_id") val staffId: Int
)

data class TaskWithAssignedStaff(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            StaffTaskAssignment::class,
            "task_id",
            "staff_id")
    )
    val assignedStaff: List<Staff>
)
