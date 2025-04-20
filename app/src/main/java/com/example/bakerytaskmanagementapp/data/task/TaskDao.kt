package com.example.bakerytaskmanagementapp.data.task

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

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
    fun getTaskById(id: Int): Task
}