package com.example.bakerytaskmanagementapp.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.data.database.repository.StaffStore
import com.example.bakerytaskmanagementapp.data.database.repository.TaskStore
import com.example.bakerytaskmanagementapp.data.utils.EntryDialogCallbacks
import com.example.bakerytaskmanagementapp.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskStore: TaskStore,
    private val staffStore: StaffStore,
): ViewModel() {
    private var _uiState = MutableStateFlow(TaskScreenUiState())
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

    private fun setupDialogCallbacks(): TaskEntryDialogCallbacks {
        return TaskEntryDialogCallbacks(
            onDismissClick = { toggleEntryDialogVisibility(false) },
            onConfirmClick = {
                saveTask()
                toggleEntryDialogVisibility(false)
            },
            onDeclineClick = { },
            onEntryDialogValueChange = ::updateDialogState
        )
    }

    fun editTask(taskWithStaff: TaskWithAssignedStaff) {
        updateDialogState(
            uiState.value.taskEntryDialogState.copy(
                task = taskWithStaff.task,
                assignedStaff = taskWithStaff.assignedStaff,
                isEditing = true
            )
        )

        toggleEntryDialogVisibility(true)
    }

    fun deleteTask(taskWithStaff: TaskWithAssignedStaff) {
        deleteTaskFromDatabase(taskWithStaff)
    }

    private fun saveTask() {
        val taskWithStaff = _uiState.value.taskEntryDialogState.toTaskWithAssignedStaff()
        if(_uiState.value.taskEntryDialogState.isEditing) {
            updateTaskInDatabase(taskWithStaff)
        } else {
            addTaskToDatabase(taskWithStaff)
        }
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

    fun toggleEntryDialogVisibility(isVisible: Boolean) {
        _uiState.update {
            if(isVisible) {
                it.copy(
                    isTaskFormVisible = true,
                    taskEntryDialogState = it.taskEntryDialogState.copy(
                        dialogCallbacks = setupDialogCallbacks()
                    )
                )
            } else {
                it.copy(
                    isTaskFormVisible = false,
                    taskEntryDialogState = TaskEntryDialogState()
                )
            }
        }
    }

    private fun updateDialogState(
        dialogState: TaskEntryDialogState = _uiState.value.taskEntryDialogState
    ) {
        val validatedState = dialogState.copy(
            isDataValid = validateDialogData(dialogState)
        )

        _uiState.update {
            it.copy(
                taskEntryDialogState = validatedState
            )
        }
    }

    private fun validateDialogData(dialogState: TaskEntryDialogState): Boolean {
        return dialogState.task.title.isNotBlank() &&
                dialogState.task.dateDeadline != null
    }
}

data class TaskScreenUiState(
    val tasksWithStaff: List<TaskWithAssignedStaff> = emptyList(),
    val isTaskFormVisible: Boolean = false,
    val taskEntryDialogState: TaskEntryDialogState = TaskEntryDialogState(),
): UiState

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

data class TaskEntryDialogState(
    val task: Task = Task(0, ""),
    val assignedStaff: List<Staff> = emptyList(),
    val isEditing: Boolean = false,
    val isDataValid: Boolean = false,
    val dialogCallbacks: TaskEntryDialogCallbacks =
        TaskEntryDialogCallbacks({}, {}, {}, {})
): UiState

fun TaskEntryDialogState.toTaskWithAssignedStaff(): TaskWithAssignedStaff {
    return TaskWithAssignedStaff(task, assignedStaff)
}

class TaskEntryDialogCallbacks(
    private val onDismissClick: () -> Unit,
    private val onConfirmClick: () -> Unit,
    private val onDeclineClick: () -> Unit,
    private val onEntryDialogValueChange: (TaskEntryDialogState) -> Unit
): EntryDialogCallbacks {
    override fun onDismiss() = onDismissClick()
    override fun onConfirm() = onConfirmClick()
    override fun onDecline() = onDeclineClick()

    override fun onValueChange(uiState: UiState) =
        onEntryDialogValueChange(uiState as TaskEntryDialogState)
}