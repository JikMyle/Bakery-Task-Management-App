package com.example.bakerytaskmanagementapp.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.AdminAccessButton
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistory
import com.example.bakerytaskmanagementapp.data.database.model.TaskHistoryAction
import com.example.bakerytaskmanagementapp.ui.AdminViewModel
import com.example.bakerytaskmanagementapp.ui.task.TaskStaffAvatarList
import com.example.bakerytaskmanagementapp.ui.task.formatDate

@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    adminViewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val adminUiState by adminViewModel.adminUiState.collectAsStateWithLifecycle()

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
                text = stringResource(R.string.task_history),
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.weight(1f))

            AdminAccessButton(
                onClick = {
                    if (adminUiState.isAdmin) {
                        adminViewModel.toggleAdminMode()
                    } else {
                        adminViewModel.toggleAdminAccessDialogVisibility(true)
                    }
                },
                isAdmin = adminUiState.isAdmin
            )
        }

        HistoryList(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            taskHistoryList = uiState.taskHistoryList,
            onItemUndoClick = viewModel::undoTaskDelete
        )
    }
}

@Composable
fun HistoryList(
    modifier: Modifier = Modifier,
    taskHistoryList: List<TaskHistoryItem>,
    onItemUndoClick: (TaskHistory) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(items = taskHistoryList) {
            HistoryListItem(
                modifier = Modifier,
                taskHistoryItem = it,
                onItemUndoClick = onItemUndoClick
            )
        }
    }
}

@Composable
private fun HistoryListItem(
    modifier: Modifier = Modifier,
    taskHistoryItem: TaskHistoryItem,
    onItemUndoClick: (TaskHistory) -> Unit
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
                text = taskHistoryItem.taskWithStaff.task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier.weight(1f))

            Text(
                text = formatDate(taskHistoryItem.dateActionDone, "MMM dd yyyy HH:mm a"),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.width(112.dp)
            )

            HistoryActionLabel(
                actionType = taskHistoryItem.actionDone,
                modifier = Modifier.padding(end = 32.dp)
            )

            TaskStaffAvatarList(
                modifier = Modifier.width(304.dp),
                staffList = taskHistoryItem.taskWithStaff.assignedStaff
            )

//            IconButton(
//                onClick = { onItemUndoClick(taskHistoryItem.toTaskHistory()) }
//            ) {
//                Icon(
//                    painter = painterResource(R.drawable.undo_24px),
//                    contentDescription = stringResource(R.string.undo),
//                )
//            }
        }
    }
}

@Composable
private fun HistoryActionLabel(
    modifier: Modifier = Modifier,
    actionType: Int,
) {
    val (label, color) = when (actionType) {
        TaskHistoryAction.DELETED -> Pair(
            stringResource(R.string.deleted),
            MaterialTheme.colorScheme.error
        )

        TaskHistoryAction.COMPLETED -> Pair(
            stringResource(R.string.completed),
            Color(colorResource(R.color.green).toArgb())
        )

        TaskHistoryAction.UPDATED -> Pair(
            stringResource(R.string.updated),
            Color(colorResource(R.color.yellow).toArgb())
        )

        TaskHistoryAction.CREATED -> Pair(
            stringResource(R.string.created),
            MaterialTheme.colorScheme.primary
        )
        else -> Pair("", Color.White)
    }

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = Color.White,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .width(112.dp)
            .background(
                color = color,
                shape = MaterialTheme.shapes.large
            )
            .padding(8.dp, 4.dp)
    )
}