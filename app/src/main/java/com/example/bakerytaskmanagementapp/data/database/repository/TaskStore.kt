package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
import com.example.bakerytaskmanagementapp.data.database.model.StaffTaskAssignment
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import kotlinx.coroutines.flow.Flow

interface TaskStore {
    fun getAllTasks(): Flow<List<Task>>
    fun getTask(id: Int): Flow<Task?>

    fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>>
    fun getTaskWithAssignedStaff(id: Int): Flow<TaskWithAssignedStaff?>

    suspend fun addTask(vararg task: Task)
    suspend fun updateTask(vararg task: Task)
    suspend fun deleteTask(vararg task: Task)

    /**
     * Inserts [StaffTaskAssignment] instance to associative table [StaffTaskAssignment]
     */
    suspend fun assignStaffToTask(vararg staffTaskAssignment: StaffTaskAssignment)

    /**
     * Removes [StaffTaskAssignment] instance from associative table [StaffTaskAssignment]
     */
    suspend fun removeStaffFromTask(vararg staffTaskAssignment: StaffTaskAssignment)
}

/**
 * Local repository for [Task] and [StaffTaskAssignment] instances
 */
class LocalTaskStore constructor(
    private val taskDao: TaskDao,
    private val staffTaskAssignmentDao: StaffTaskAssignmentDao,
): TaskStore {
    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getTask(id: Int): Flow<Task?> = taskDao.getTask(id)

    override fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>> =
        taskDao.getAllTasksWithAssignedStaff()

    override fun getTaskWithAssignedStaff(id: Int): Flow<TaskWithAssignedStaff?> =
        taskDao.getTaskWithAssignedStaff(id)

    override suspend fun addTask(vararg task: Task) {
        taskDao.addTask(*task)
    }

    override suspend fun updateTask(vararg task: Task) {
        taskDao.updateTask(*task)
    }

    override suspend fun deleteTask(vararg task: Task) {
        taskDao.deleteTask(*task)
    }

    override suspend fun assignStaffToTask(vararg staffTaskAssignment: StaffTaskAssignment) {
        staffTaskAssignmentDao.assignStaffToTask(*staffTaskAssignment)
    }

    override suspend fun removeStaffFromTask(vararg staffTaskAssignment: StaffTaskAssignment) {
        staffTaskAssignmentDao.removeStaffFromTask(*staffTaskAssignment)
    }
}

