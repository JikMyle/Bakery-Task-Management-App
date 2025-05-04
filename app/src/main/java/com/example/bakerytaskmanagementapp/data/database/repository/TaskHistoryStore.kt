package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskHistoryDao
import com.example.bakerytaskmanagementapp.data.database.dao.TransactionRunner
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistory
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistoryAction
import com.example.bakerytaskmanagementapp.data.database.model.TaskStatusType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.Date
import javax.inject.Inject

interface TaskHistoryStore {
    fun getAllHistory(): Flow<List<TaskHistory>>
    fun getHistoryItem(id: Int): Flow<TaskHistory?>

    fun generateTaskHistoryItem(taskId: Int, title: String, actionDone: Int): TaskHistory
    suspend fun undoTaskDelete(taskHistory: TaskHistory)
}

/**
 * Local repository for [TaskHistory] instances
 */
class LocalTaskHistoryStore @Inject constructor(
    private val taskDao: TaskDao,
    private val taskHistoryDao: TaskHistoryDao,
    private val transactionRunner: TransactionRunner
): TaskHistoryStore {
    override fun getAllHistory(): Flow<List<TaskHistory>> =
        taskHistoryDao.getAllHistory()

    override fun getHistoryItem(id: Int): Flow<TaskHistory?> =
        taskHistoryDao.getHistoryItem(id)

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

    override suspend fun undoTaskDelete(taskHistory: TaskHistory) {
        transactionRunner {
            val task = taskDao.getTask(taskHistory.taskId)
                .firstOrNull() ?: return@transactionRunner

            // Changes task status back to PENDING and removes dateDeleted
            taskDao.updateTask(
                task.copy(
                    status = TaskStatusType.PENDING,
                    dateLastUpdated = Date(),
                    dateDeleted = null,
                )
            )

            // Adds an item to [TaskHistory] with action [TaskHistoryAction.UPDATED]
            taskHistoryDao.addToHistory(
                generateTaskHistoryItem(
                    taskId = task.id,
                    title = task.title,
                    actionDone = TaskHistoryAction.UPDATED
                )
            )
        }
    }
}