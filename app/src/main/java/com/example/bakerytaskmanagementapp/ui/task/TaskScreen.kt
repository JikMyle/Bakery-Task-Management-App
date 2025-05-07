package com.example.bakerytaskmanagementapp.ui.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff


@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val uiState by  viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(2.dp)
    ) {
        items(items = uiState.tasksWithStaff) {
            TaskListItem(
                modifier = Modifier,
                taskWithAssignedStaff = it,
                onItemEditClick = viewModel::editTask,
                onItemDeleteClick = viewModel::deleteTask
            )
        }
    }
}

@Composable
private fun TaskListItem(
    modifier: Modifier = Modifier,
    taskWithAssignedStaff: TaskWithAssignedStaff,
    onItemEditClick: (TaskWithAssignedStaff) -> Unit,
    onItemDeleteClick: (Task) -> Unit,
) {
    ElevatedCard(
        modifier = modifier.height(80.dp),
        shape = MaterialTheme.shapes.small,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = taskWithAssignedStaff.task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // This [SPACER] is to separate the left and right contents
            Spacer(modifier.weight(1f))

            IconButton(
                onClick = { /* TODO */ }
            ) {
                Icon(
                    imageVector = if(taskWithAssignedStaff.task.isPriority) {
                        Icons.Filled.Star
                    } else {
                        Icons.Outlined.Star
                    },
                    contentDescription =
                        stringResource(R.string.toggle_task_priority_button_content_description)
                )
            }

            IconButton(
                onClick = { onItemEditClick(taskWithAssignedStaff) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription =
                        stringResource(R.string.edit_staff_button_content_description)
                )
            }

            IconButton(
                onClick = { onItemDeleteClick(taskWithAssignedStaff.task) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription =
                        stringResource(R.string.delete_staff_button_content_description)
                )
            }
        }
    }
}