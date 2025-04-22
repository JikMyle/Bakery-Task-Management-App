package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.data.database.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTask(vararg task: Task)

    @Update
    suspend fun updateTask(vararg task: Task)

    @Delete
    suspend fun deleteTask(vararg task: Task)

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: Int): Task

    @Transaction
    @Query("SELECT * FROM task")
    fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTaskWithAssignedStaff(id: Int): TaskWithAssignedStaff
}