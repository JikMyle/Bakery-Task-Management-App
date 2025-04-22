package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.bakerytaskmanagementapp.data.database.model.StaffTaskAssignment

@Dao
interface StaffTaskAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun assignStaffToTask(vararg staffTaskAssignment: StaffTaskAssignment)

    @Delete
    suspend fun removeStaffFromTask(vararg staffTaskAssignment: StaffTaskAssignment)
}