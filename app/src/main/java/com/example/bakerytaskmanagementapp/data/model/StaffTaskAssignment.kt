package com.example.bakerytaskmanagementapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class StaffTaskAssignment(
    @ColumnInfo(name = "task_id") val taskId: Int,
    @ColumnInfo(name = "staff_id") val staffId: Int
)

data class TaskWithAssignedStaff(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "task_id",
        entityColumn = "staff_id",
        associateBy = Junction(StaffTaskAssignment::class)
    )
    val assignedStaff: List<Staff>
)
