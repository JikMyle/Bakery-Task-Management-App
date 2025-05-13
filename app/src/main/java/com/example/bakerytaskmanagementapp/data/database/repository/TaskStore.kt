package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskHistoryDao
import com.example.bakerytaskmanagementapp.data.database.dao.TransactionRunner
import com.example.bakerytaskmanagementapp.data.database.model.StaffTaskAssignment
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistory
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistoryAction
import com.example.bakerytaskmanagementapp.data.database.model.TaskStatusType
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject

interface TaskStore {
    fun getAllTasks(): Flow<List<Task>>
    fun getTask(id: Int): Flow<Task?>

    fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>>
    fun getTaskWithAssignedStaff(id: Int): Flow<TaskWithAssignedStaff?>

    suspend fun addTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff)
    suspend fun updateTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff)
    suspend fun deleteTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff)

    fun generateTaskHistoryItem(taskId: Int, title: String, actionDone: Int): TaskHistory
}

/**
 * Local repository for [Task] and [StaffTaskAssignment] instances
 */
class LocalTaskStore @Inject constructor(
    private val taskDao: TaskDao,
    private val staffTaskAssignmentDao: StaffTaskAssignmentDao,
    private val taskHistoryDao: TaskHistoryDao,
    private val transactionRunner: TransactionRunner
): TaskStore {
    override fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    override fun getTask(id: Int): Flow<Task?> = taskDao.getTask(id)

    override fun getAllTasksWithAssignedStaff(): Flow<List<TaskWithAssignedStaff>> =
        taskDao.getAllTasksWithAssignedStaff()

    override fun getTaskWithAssignedStaff(id: Int): Flow<TaskWithAssignedStaff?> =
        taskDao.getTaskWithAssignedStaff(id)

    override suspend fun addTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff) {
        taskWithAssignedStaff.forEach {
            transactionRunner {
                val taskId = taskDao.addTask(it.task)    // Inserts task to table

                // Inserts an item in the [TaskHistory]
                taskHistoryDao.addToHistory(
                    generateTaskHistoryItem(taskId[0].toInt(), it.task.title, TaskHistoryAction.CREATED)
                )

                it.assignedStaff.forEach { staff ->     // Assigns staff to task
                    staffTaskAssignmentDao.assignStaffToTask(
                        StaffTaskAssignment(taskId[0].toInt(), staff.id)
                    )
                }
            }
        }
    }

    override suspend fun updateTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff) {
        taskWithAssignedStaff.forEach {
            transactionRunner {
                taskDao.updateTask(it.task)

                /**
                 * Code below may need improvement.
                 * In summary, to update the list of assigned staff, it first fetches
                 * the old list and compares with the new list. Staff unique to the old list
                 * will be unassigned, while staff unique to the new list will be assigned.
                 * The staff found in both will be ignored.
                 */

                // Gets old list of staff assigned to task
                val oldStaff = taskDao.getTaskWithAssignedStaff(it.task.id)
                    .firstOrNull()?.assignedStaff?.toSet()

                // Gets new list of staff assigned to task
                val newStaff = it.assignedStaff.toSet()

                // Removes common staff from old list, remaining will be unassigned from task
                val oldDiff = oldStaff?.subtract(newStaff)

                // Removes common staff from new list, remaining will be assigned to task
                val newDiff = if (oldStaff != null) {
                    newStaff.subtract(oldStaff)
                } else {
                    newStaff
                }

                // Unassigns old staff from task
                oldDiff?.forEach { staff ->
                    staffTaskAssignmentDao.removeStaffFromTask(
                        StaffTaskAssignment(it.task.id, staff.id)
                    )
                }

                // Assigns new staff to task
                newDiff.forEach { staff ->
                    staffTaskAssignmentDao.assignStaffToTask(
                        StaffTaskAssignment(it.task.id, staff.id)
                    )
                }

                // Inserts an item in the [TaskHistory]
                taskHistoryDao.addToHistory(
                    generateTaskHistoryItem(
                        it.task.id,
                        it.task.title,
                        if(it.task.status == TaskStatusType.COMPLETED) {
                            TaskHistoryAction.COMPLETED
                        } else {
                            TaskHistoryAction.UPDATED
                        }
                    )
                )
            }
        }
    }

    override suspend fun deleteTask(vararg taskWithAssignedStaff: TaskWithAssignedStaff) {
        taskWithAssignedStaff.forEach {
            val deletedTask = it.task.copy(
                status = TaskStatusType.DELETED,
                dateLastUpdated = Date(),
                dateDeleted = Date()
            )

            transactionRunner {
                // Task status is changed to DELETED instead of removing it from database
                taskDao.updateTask(deletedTask)

                // Inserts an item to [TaskHistory]
                taskHistoryDao.addToHistory(
                    generateTaskHistoryItem(it.task.id, it.task.title, TaskHistoryAction.DELETED)
                )
            }
        }
    }

    override fun generateTaskHistoryItem(
        taskId: Int,
        title: String,
        actionDone: Int
    ): TaskHistory {
        return TaskHistory(
            id = 0,
            taskId = taskId,
            actionDone = actionDone,
            dateActionDone = Date(),
        )
    }
}

