package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.model.Staff
import kotlinx.coroutines.flow.Flow

interface StaffStore {
    fun getAllStaff(): Flow<List<Staff>>
    suspend fun getStaff(id: Int): Staff?

    suspend fun addStaff(vararg staff: Staff)
    suspend fun updateStaff(vararg staff: Staff)
    suspend fun deleteStaff(vararg staff: Staff)
}