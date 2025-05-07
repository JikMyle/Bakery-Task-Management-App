package com.example.bakerytaskmanagementapp.ui.task

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskStatusType
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.ui.staff.ProfileAvatar


@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    viewModel: TaskViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by  viewModel.uiState.collectAsStateWithLifecycle()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()

    // The following block is for displaying toast messages
    LaunchedEffect(operationState) {
        when (val state = operationState) {
            is OperationState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetOperationState() // Clear after showing
            }
            is OperationState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                viewModel.resetOperationState() // Clear after showing
            }
            else -> { /* No action needed */ }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                modifier = Modifier,
                text = stringResource(R.string.manage_tasks),
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            AddTaskButton{
                viewModel.toggleFormVisibility(true)
            }
        }

        TaskList(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            taskList = uiState.tasksWithStaff,
            onItemTogglePriority = viewModel::toggleTaskPriority,
            onItemDeleteClick = viewModel::deleteTask,
            onItemEditClick = viewModel::editTask
        )
    }
}

@Composable
private fun TaskList(
    modifier: Modifier = Modifier,
    taskList: List<TaskWithAssignedStaff>,
    onItemTogglePriority: (TaskWithAssignedStaff) -> Unit,
    onItemEditClick: (TaskWithAssignedStaff) -> Unit,
    onItemDeleteClick: (TaskWithAssignedStaff) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(items = taskList.filter { it.task.status == TaskStatusType.PENDING }) {
            TaskListItem(
                modifier = Modifier,
                taskWithAssignedStaff = it,
                onItemTogglePriority = onItemTogglePriority,
                onItemEditClick = onItemEditClick,
                onItemDeleteClick = onItemDeleteClick
            )
        }
    }
}

@Composable
private fun TaskListItem(
    modifier: Modifier = Modifier,
    taskWithAssignedStaff: TaskWithAssignedStaff,
    onItemTogglePriority: (TaskWithAssignedStaff) -> Unit,
    onItemEditClick: (TaskWithAssignedStaff) -> Unit,
    onItemDeleteClick: (TaskWithAssignedStaff) -> Unit,
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
                onClick = { onItemTogglePriority(taskWithAssignedStaff) },
            ) {
                // Filled yellow star for priority tasks
                if(taskWithAssignedStaff.task.isPriority) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription =
                            stringResource(R.string.set_task_as_priority),
                        tint = Color(colorResource(R.color.star_yellow).toArgb()),
                        modifier = Modifier.size(32.dp)
                    )
                } else { // Empty star for non-priority tasks
                    Icon(
                        imageVector = Icons.TwoTone.Star,
                        contentDescription =
                            stringResource(R.string.remove_task_from_priority),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Text(
                text = taskWithAssignedStaff.task.let { it.dateDeadline
                    ?.let { date ->  it.formatDate(date) } ?: ""
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(112.dp)
            )

            TaskDueInText(task = taskWithAssignedStaff.task)

            IconButton(
                onClick = { /*TODO*/ },
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription =
                        stringResource(R.string.assign_staff_to_task),
                )
            }

            TaskStaffAvatarList(
                modifier = Modifier.width(272.dp),
                staffList = taskWithAssignedStaff.assignedStaff
            )

            TaskListItemDropdownMenu(
                onItemEditClick = { onItemEditClick(taskWithAssignedStaff) },
                onItemDeleteClick = { onItemDeleteClick(taskWithAssignedStaff) }
            )
        }
    }
}

@Composable
private fun TaskDueInText(
    modifier: Modifier = Modifier,
    task: Task,
) {
    Text(
        text =  task.dateDeadline?.let {
            val unit = stringArrayResource(R.array.time_units_abbreviated)
            val prefix = stringResource(R.string.due_in) + " "
            if (task.getDueInDays()!! > 0)
                prefix + task.getDueInDays().toString() + unit[0]
            else if (task.getDueInHours()!! > 0)
                prefix + task.getDueInHours().toString() + unit[1]
            else if (task.getDueInMinutes()!! > 0)
                prefix + task.getDueInMinutes().toString() + unit[2]
            else stringResource(R.string.due_passed)
        } ?: stringResource(R.string.no_due_date),
        style = MaterialTheme.typography.labelLarge,
        color = Color.White,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .width(112.dp)
            .background(
                color = task.getDueInMinutes()?.let {
                        if(it < 0) {
                            Color.Red
                        } else {
                            Color(colorResource(R.color.star_yellow).toArgb())
                        }
                    } ?: MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large
            )
            .padding(8.dp, 4.dp)
    )
}

@Composable
private fun TaskStaffAvatarList(
    modifier: Modifier = Modifier,
    staffList: List<Staff>
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        val maxCount = 5
        val avatarOffset = -16

        LazyRow {
            items(staffList.take(maxCount)) { staff ->
                ProfileAvatar(
                    letter = staff.firstName[0].toString(),
                    modifier = Modifier
                        .offset(x = (staffList.indexOf(staff) * avatarOffset).dp)
                        .border(
                            width = 1.dp,
                            color = CardDefaults.elevatedCardColors().containerColor,
                            shape = MaterialTheme.shapes.extraLarge
                        )
                )
            }
        }
        if(staffList.count() > maxCount) {
            Text(
                text = "+${staffList.count() - maxCount}",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .offset(((maxCount - 1) * avatarOffset).dp)
            )
        }
    }
}

@Composable
private fun TaskListItemDropdownMenu(
    modifier: Modifier = Modifier,
    onItemEditClick: () -> Unit,
    onItemDeleteClick: () -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box {
        IconButton(
            modifier = modifier,
            onClick = { isExpanded = true },
        ) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more_options),
            )
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(
                    text = stringResource(R.string.edit_task),
                    style = MaterialTheme.typography.labelLarge
                ) },
                onClick = {
                    isExpanded = false
                    onItemEditClick()
                }
            )

            DropdownMenuItem(
                text = { Text(
                    text = stringResource(R.string.delete_task),
                    style = MaterialTheme.typography.labelLarge
                ) },
                onClick = {
                    isExpanded = false
                    onItemDeleteClick()
                },
            )
        }
    }
}

@Composable
private fun AddTaskButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(24.dp, 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.add_task),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(
                    R.string.add_task
                )
            )
        }
    }
}