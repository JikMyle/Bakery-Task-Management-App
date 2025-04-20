package com.example.bakerytaskmanagementapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.model.Staff

@Dao
interface StaffDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addStaff(vararg staff: Staff)

    @Update
    fun updateStaff(vararg staff: Staff)

    @Delete
    fun deleteStaff(vararg staff: Staff)

    @Query("SELECT * FROM staff")
    fun getAllStaff(): List<Staff>

    @Query("Select * FROM staff WHERE id = :id")
    fun getStaffById(id: Int): Staff

    @Query("Select * FROM staff WHERE first_name LIKE :first " +
            "AND last_name LIKE :last")
    fun getStaffByName(first: String, last: String): Staff
}