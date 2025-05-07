package com.example.bakerytaskmanagementapp.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.data.database.repository.StaffStore
import com.example.bakerytaskmanagementapp.data.database.repository.TaskStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskStore: TaskStore,
    private val staffStore: StaffStore,
): ViewModel() {
    private var _uiState = MutableStateFlow(TaskScreenState())
    val uiState = _uiState.asStateFlow()

    private var _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            taskStore.getAllTasksWithAssignedStaff().collect { tasksWithStaff ->
                _uiState.value = _uiState.value.copy(
                    tasksWithStaff = tasksWithStaff
                )
            }
        }
    }

    private fun addTaskToDatabase(taskWithStaff: TaskWithAssignedStaff) {
        viewModelScope.launch {
            try {
                taskStore.addTask(taskWithStaff)
                val message = "Task added successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Failed to add task"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }


    private fun updateTaskInDatabase(taskWithStaff: TaskWithAssignedStaff) {
        viewModelScope.launch {
            try {
                taskStore.updateTask(taskWithStaff)
                val message = "Task updated successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Failed to update task"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    private fun deleteTaskFromDatabase(taskWithStaff: TaskWithAssignedStaff) {
        viewModelScope.launch {
            try {
                taskStore.deleteTask(taskWithStaff)
                val message = "Task deleted successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Failed to delete task"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    fun resetOperationState() {
        _operationState.update { OperationState.Idle }
    }

    fun editTask(taskWithStaff: TaskWithAssignedStaff) {
        // TODO: Update task in database
    }

    fun deleteTask(taskWithStaff: TaskWithAssignedStaff) {
        deleteTaskFromDatabase(taskWithStaff)
    }

    /**
     * Note: Rapid toggling may cause toast messages to queue up
     */
    fun toggleTaskPriority(taskWithStaff: TaskWithAssignedStaff) {
        val updatedTask = taskWithStaff.task.let {
            it.copy(
                isPriority = !it.isPriority
            )
        }

        val updatedTaskWithStaff = taskWithStaff.copy(
            task = updatedTask
        )

        updateTaskInDatabase(updatedTaskWithStaff)
    }

    fun toggleFormVisibility(isVisible: Boolean) {
        _uiState.value = _uiState.value.copy(
            isTaskFormVisible = isVisible
        )
    }
}

data class TaskScreenState(
    val tasksWithStaff: List<TaskWithAssignedStaff> = emptyList(),
    val isTaskFormVisible: Boolean = false,
)

fun Task.formatDate(date: Date): String {
    val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun Task.getDueInDays(): Long? {
    val diff = dateDeadline?.time?.minus(Date().time)
    return diff?.milliseconds?.inWholeDays
}

fun Task.getDueInHours(): Long? {
    val diff = dateDeadline?.time?.minus(Date().time)
    return diff?.milliseconds?.inWholeHours
}

fun Task.getDueInMinutes(): Long? {
    val diff = dateDeadline?.time?.minus(Date().time)
    return diff?.milliseconds?.inWholeMinutes
}