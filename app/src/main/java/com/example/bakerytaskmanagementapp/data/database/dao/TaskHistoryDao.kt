package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToHistory(vararg taskHistory: TaskHistory)

    @Delete
    suspend fun deleteFromHistory(vararg taskHistory: TaskHistory)

    @Query("SELECT * FROM task_history")
    fun getAllHistory(): Flow<List<TaskHistory>>

    @Query("SELECT * FROM task_history WHERE id = :id")
    fun getHistoryItem(id: Int): Flow<TaskHistory?>
}