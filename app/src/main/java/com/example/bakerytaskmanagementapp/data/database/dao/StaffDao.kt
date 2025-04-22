package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import kotlinx.coroutines.flow.Flow

@Dao
interface StaffDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStaff(vararg staff: Staff)

    @Update
    suspend fun updateStaff(vararg staff: Staff)

    @Delete
    suspend fun deleteStaff(vararg staff: Staff)

    @Query("SELECT * FROM staff")
    fun getAllStaff(): Flow<List<Staff>>

    @Query("Select * FROM staff WHERE id = :id")
    fun getStaffById(id: Int): Flow<Staff?>

    @Query("Select * FROM staff WHERE first_name LIKE :first " +
            "AND last_name LIKE :last")
    suspend fun getStaffByName(first: String, last: String): Staff?
}