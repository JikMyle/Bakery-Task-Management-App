package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.StaffDao
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface StaffStore {
    fun getAllStaff(): Flow<List<Staff>>
    fun getStaffById(id: Int): Flow<Staff?>
    suspend fun getStaffByName(first: String, last: String): Staff?

    /**
     * Adds staff to the database, can pass multiple [Staff] instances
     */
    suspend fun addStaff(vararg staff: Staff)

    /**
     * Removes staff to the database, can pass multiple [Staff] instances
     */
    suspend fun updateStaff(vararg staff: Staff)

    /**
     * Removes staff to the database, can pass multiple [Staff] instances
     */
    suspend fun deleteStaff(vararg staff: Staff)
}

/**
 * Local data repository for [Staff] instances
 */
class LocalStaffStore @Inject constructor(
    private val staffDao: StaffDao
): StaffStore {
    override fun getAllStaff(): Flow<List<Staff>> = staffDao.getAllStaff()
    override fun getStaffById(id: Int): Flow<Staff?> = staffDao.getStaffById(id)

    override suspend fun getStaffByName(first: String, last: String): Staff? =
        staffDao.getStaffByName(first, last)

    override suspend fun addStaff(vararg staff: Staff) {
        staffDao.addStaff(*staff)
    }

    override suspend fun updateStaff(vararg staff: Staff) {
        staffDao.updateStaff(*staff)
    }

    override suspend fun deleteStaff(vararg staff: Staff) {
        staffDao.deleteStaff(*staff)
    }
}