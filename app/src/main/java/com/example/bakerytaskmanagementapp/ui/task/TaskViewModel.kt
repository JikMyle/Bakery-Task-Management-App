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
import kotlinx.coroutines.launch
import javax.inject.Inject

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
                _operationState.value = OperationState.Success(message)
            } catch (e: Exception) {
                val message = "Failed to add task"
                _operationState.value = OperationState.Error(message)
            }
        }
    }


    private fun updateTaskInDatabase(taskWithStaff: TaskWithAssignedStaff) {
        viewModelScope.launch {
            try {
                taskStore.updateTask(taskWithStaff)
                val message = "Task updated successfully"
                _operationState.value = OperationState.Success(message)
            } catch (e: Exception) {
                val message = "Failed to update task"
                _operationState.value = OperationState.Error(message)
            }
        }
    }

    private fun deleteTaskFromDatabase(taskWithStaff: TaskWithAssignedStaff) {
        viewModelScope.launch {
            try {
                taskStore.deleteTask(taskWithStaff)
                val message = "Task deleted successfully"
                _operationState.value = OperationState.Success(message)
            } catch (e: Exception) {
                val message = "Failed to delete task"
                _operationState.value = OperationState.Error(message)
            }
        }
    }

    fun editTask(taskWithStaff: TaskWithAssignedStaff) {
        // TODO: Update task in database
    }

    fun deleteTask(task: Task) {
        // TODO: Delete task from database
    }
}

data class TaskScreenState(
    val tasksWithStaff: List<TaskWithAssignedStaff> = emptyList(),
    val isTaskFormVisible: Boolean = false,
)