package com.example.bakerytaskmanagementapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.data.model.Task

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTask(vararg task: Task)

    @Update
    fun updateTask(vararg task: Task)

    @Delete
    fun deleteTask(vararg task: Task)

    @Query("SELECT * FROM task")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Int): Task

    @Transaction
    @Query("SELECT * FROM task")
    fun getAllTasksWithAssignedStaff(): List<TaskWithAssignedStaff>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskWithAssignedStaff(id: Int): TaskWithAssignedStaff
}