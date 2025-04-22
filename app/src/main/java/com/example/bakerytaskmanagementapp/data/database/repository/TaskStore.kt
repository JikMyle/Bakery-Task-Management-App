package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import kotlinx.coroutines.flow.Flow

interface TaskStore {
    fun getAllTasks(): Flow<List<Task>>
    suspend fun getTask(id: Int): Task?

    fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>>
    suspend fun getTaskWithAssignedStaff(id: Int): TaskWithAssignedStaff?

    suspend fun addTask(vararg task: Task)
    suspend fun updateTask(vararg task: Task)
    suspend fun deleteTask(vararg task: Task)

    suspend fun assignStaffToTask(vararg staffTaskAssignmentDao: StaffTaskAssignmentDao)
    suspend fun removeStaffFromTask(vararg staffTaskAssignmentDao: StaffTaskAssignmentDao)
}

