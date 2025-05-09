package com.example.bakerytaskmanagementapp.ui.task

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
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
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.model.Task
import com.example.bakerytaskmanagementapp.data.database.model.TaskStatusType
import com.example.bakerytaskmanagementapp.data.database.model.TaskWithAssignedStaff
import com.example.bakerytaskmanagementapp.ui.staff.ProfileAvatar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


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

    if(uiState.isTaskEntryDialogVisible) {
        TaskEntryDialog(
            dialogState = uiState.taskEntryDialogState
        )
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
                viewModel.toggleEntryDialogVisibility(true)
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
                text = taskWithAssignedStaff.task.dateDeadline?.let{
                    formatDate(it)
                } ?: "",
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
                    if (it < 0) {
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

/*
 * Composable for the Task Entry Dialog
 * DevNote: Becomes confusing to look at because of the nested composables
 */
@Composable
private fun TaskEntryDialog(
    modifier: Modifier = Modifier,
    dialogState: TaskEntryDialogState
) {
    val callbacks = dialogState.dialogCallbacks
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var isTimePickerVisible by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = callbacks::onDismiss
    ) {
        Card(modifier = modifier.width(300.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if(dialogState.isEditing) stringResource(R.string.edit_task)
                    else stringResource(R.string.add_task),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = dialogState.task.title,
                    onValueChange = {
                        callbacks.onValueChange(
                            dialogState.copy(
                                task = dialogState.task.copy(
                                    title = it
                                )
                            )
                        ) },
                    label = { Text(text = stringResource(R.string.title)) }
                )

                /*
                    DatePicker text field and dialog is very long compared to other parts
                    Might need to refactor later, or modularize these types of composables
                 */
                OutlinedTextField(
                    value = dialogState.task.dateDeadline?.let {
                        formatDate(it, "MM/dd/yyyy")
                    } ?: "",
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.end_date)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = stringResource(R.string.select_date),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(dialogState.task.dateDeadline?.let {
                            formatDate(it, "MM/dd/yyyy")
                        } ?: "") {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    isDatePickerVisible = true
                                }
                            }
                        }
                )

                if(isDatePickerVisible) {
                    TaskEntryFormDatePickerDialog(
                        date = dialogState.task.dateDeadline,
                        onDismiss = { isDatePickerVisible = false },
                        onConfirm = { newDate ->
                            val oldDate = dialogState.task.dateDeadline

                            callbacks.onValueChange(
                                dialogState.copy(
                                    task = dialogState.task.copy(
                                        dateDeadline = oldDate?.let {
                                            replaceDate(it, newDate)
                                        } ?: newDate
                                    )
                                )
                            )

                            isDatePickerVisible = false
                        },
                    )
                }

                /*
                    Same issue with the TimePicker text field and dialog as above
                 */
                OutlinedTextField(
                    value = dialogState.task.dateDeadline?.let {
                        formatDate(it, "hh:mm a")
                    } ?: "",
                    onValueChange = {},
                    label = { Text(text = stringResource(R.string.end_time)) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = stringResource(R.string.select_time),
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(dialogState.task.dateDeadline?.let {
                            formatDate(it, "hh:mm a")
                        } ?: "") {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                if (upEvent != null) {
                                    isTimePickerVisible = true
                                }
                            }
                        }
                )

                if(isTimePickerVisible) {
                    TaskEntryFormTimePickerDialog(
                        time = dialogState.task.dateDeadline,
                        onDismiss = { isTimePickerVisible = false },
                        onConfirm = { newTime ->
                            val oldTime = dialogState.task.dateDeadline

                            callbacks.onValueChange(
                                dialogState.copy(
                                    task = dialogState.task.copy(
                                        dateDeadline = oldTime?.let {
                                            replaceTime(it, newTime)
                                        } ?: newTime
                                    )
                                )
                            )

                            isTimePickerVisible = false
                        }
                    )
                }

                // Bottom Dialog Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    // Cancellation Button
                    TextButton(
                        onClick = callbacks::onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Confirmation Button
                    TextButton(
                        onClick = callbacks::onConfirm,
                        enabled = dialogState.isDataValid
                    ) {
                        Text(
                            text = if(dialogState.isEditing)
                                stringResource(R.string.update) else stringResource(R.string.add),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEntryFormDatePickerDialog(
    modifier: Modifier = Modifier,
    date: Date?,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit,
) {
    val datePickerState = rememberDatePickerState()
    date?.let{ datePickerState.selectedDateMillis = it.time}

    DatePickerDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedDate = Date(datePickerState.selectedDateMillis!!)
                    onConfirm(selectedDate)
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEntryFormTimePickerDialog(
    modifier: Modifier = Modifier,
    time: Date?,
    onDismiss: () -> Unit,
    onConfirm: (Date) -> Unit,
) {
    val timePickerState = rememberTimePickerState(is24Hour = false)

    time?.let { // If time is not null, then timePickerState should be updated
        val selectedTime = Calendar.getInstance()
        selectedTime.time = it

        timePickerState.hour = selectedTime.get(Calendar.HOUR_OF_DAY)
        timePickerState.minute = selectedTime.get(Calendar.MINUTE)
    }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    text = stringResource(R.string.select_time),
                    style = MaterialTheme.typography.labelMedium
                )

                TimePicker(timePickerState)

                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    TextButton(
                        onClick = {
                            val newTime = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                                set(Calendar.MINUTE, timePickerState.minute)
                            }

                            onConfirm(newTime.time)
                        },
                        enabled = timePickerState.hour != -1 && timePickerState.minute != -1
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }

            }
        }
    }
}

/*
    DevNote: Might need to change Date objects in DB models to Calendar instance.
    It might be easier to manage and update.
 */
fun formatDate(date: Date, pattern: String = "MMM dd yyyy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(date)
}

fun replaceDate(oldDate: Date, newDate: Date): Date {
    val oldCalendar = Calendar.getInstance().apply { time = oldDate }
    val newCalendar = Calendar.getInstance().apply { time = newDate }

    oldCalendar.set(Calendar.YEAR, newCalendar.get(Calendar.YEAR))
    oldCalendar.set(Calendar.MONTH, newCalendar.get(Calendar.MONTH))
    oldCalendar.set(Calendar.DAY_OF_MONTH, newCalendar.get(Calendar.DAY_OF_MONTH))

    return oldCalendar.time
}

fun replaceTime(oldTime: Date, newTime: Date): Date {
    val oldCalendar = Calendar.getInstance().apply { time = oldTime }
    val newCalendar = Calendar.getInstance().apply { time = newTime }

    oldCalendar.set(Calendar.HOUR_OF_DAY, newCalendar.get(Calendar.HOUR_OF_DAY))
    oldCalendar.set(Calendar.MINUTE, newCalendar.get(Calendar.MINUTE))
    oldCalendar.set(Calendar.SECOND, 0)

    return oldCalendar.time
}