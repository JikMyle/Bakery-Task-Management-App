package com.example.bakerytaskmanagementapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistory
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.data.database.repository.TaskHistoryStore
import com.example.bakerytaskmanagementapp.data.database.repository.TaskStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val taskStore: TaskStore,
    private val taskHistoryStore: TaskHistoryStore
): ViewModel() {
    private var _uiState = MutableStateFlow(HistoryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            taskHistoryStore.getAllHistory().collect { items ->
                val historyItems = items.mapNotNull { item ->
                    taskStore.getTaskWithAssignedStaff(item.taskId).firstOrNull()?.let {
                        TaskHistoryItem(
                            id = item.id,
                            taskWithStaff = it,
                            actionDone = item.actionDone,
                            dateActionDone = item.dateActionDone
                        )
                    }
                }

                _uiState.update { state ->
                    state.copy(
                        taskHistoryList = historyItems.sortedByDescending { it.dateActionDone }
                    )
                }
            }
        }
    }

    /*
        DevNote: Undo task deletion or mark as complete are the same actions
        that set the task status to PENDING.

        If app needs actual history undo function, try saving snapshots of tasks as json strings
        in [TaskHistory] table.
     */
    fun undoTaskDelete(taskHistory: TaskHistory) {
        viewModelScope.launch {
            taskHistoryStore.undoTaskDelete(taskHistory)
        }
    }
}

data class HistoryUiState(
    val taskHistoryList: List<TaskHistoryItem> = emptyList()
)

data class TaskHistoryItem(
    val id: Int,
    val taskWithStaff: TaskWithAssignedStaff,
    val actionDone: Int,
    val dateActionDone: Date,
)

fun TaskHistoryItem.toTaskHistory(): TaskHistory {
    return TaskHistory(
        id = id,
        taskId = taskWithStaff.task.id,
        actionDone = actionDone,
        dateActionDone = dateActionDone,
    )
}