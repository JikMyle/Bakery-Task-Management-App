package com.example.bakerytaskmanagementapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.bakerytaskmanagementapp.data.model.StaffTaskAssignment

@Dao
interface StaffTaskAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun assignStaffToTask(vararg staffTaskAssignment: StaffTaskAssignment)

    @Delete
    fun removeStaffFromTask(vararg staffTaskAssignment: StaffTaskAssignment)
}