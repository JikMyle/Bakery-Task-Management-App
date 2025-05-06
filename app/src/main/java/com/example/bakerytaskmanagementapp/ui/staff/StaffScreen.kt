package com.example.bakerytaskmanagementapp.ui.staff

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Staff


@Composable
fun StaffScreen(
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsState()
    val operationState by viewModel.operationState.collectAsStateWithLifecycle()

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

    if(uiState.value.isStaffFormVisible) {
        StaffFormDialog(staffFormState = uiState.value.staffFormState)
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
                text = stringResource(R.string.manage_staff),
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            AddStaffButton(onClick = {
                viewModel.toggleStaffFormVisibility(true)
            })
        }

        StaffList(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            staffList = uiState.value.staff,
            onItemDeleteClick = viewModel::deleteStaff,
            onItemEditClick = viewModel::editStaff
        )
    }
}

@Composable
private fun AddStaffButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
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
                text = stringResource(R.string.add_staff),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(
                    R.string.add_staff_button_content_description
                )
            )
        }
    }
}

@Composable
private fun StaffList(
    modifier: Modifier = Modifier,
    staffList: List<Staff>,
    onItemEditClick: (Staff) -> Unit,
    onItemDeleteClick: (Staff) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(items = staffList) { staff ->
            StaffListItem(
                modifier = Modifier,
                staff = staff,
                onItemEditClick = onItemEditClick,
                onItemDeleteClick = onItemDeleteClick
            )
        }
    }
}

@Composable
private fun StaffListItem(
    modifier: Modifier = Modifier,
    staff: Staff,
    onItemEditClick: (Staff) -> Unit = {},
    onItemDeleteClick: (Staff) -> Unit = {},
) {
    ElevatedCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfileAvatar(
                modifier = Modifier.padding(16.dp, 16.dp),
                letter = staff.firstName[0].toString()
            )

            Text(
                text = "${staff.firstName} ${staff.lastName}",
                style = MaterialTheme.typography.bodyLarge,
            )

            // This [SPACER] is to separate the left and right contents
            Spacer(modifier.weight(1f))

            IconButton(
                onClick = { onItemEditClick(staff) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null /* TODO */
                )
            }

            IconButton(
                onClick = { onItemDeleteClick(staff) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null /* TODO */
                )
            }
        }
    }
}

/**
 * App currently does not support image profiles,
 * thus only letters are used as avatars
 */
@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    letter: String,
) {
    Box(
        modifier = modifier
            .size(36.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = letter,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StaffFormDialog(
    modifier: Modifier = Modifier,
    staffFormState: StaffFormState
) {
    Dialog(
        onDismissRequest = staffFormState.onDismiss
    ) {
        Card(modifier = modifier.width(300.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if(staffFormState.isStaffFormEditing) "Edit Staff" else "Add Staff",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = staffFormState.firstName,
                    onValueChange = {
                        staffFormState.onValueChange(
                            staffFormState.copy(
                                firstName = it
                            )
                        ) },
                    label = { Text(text = stringResource(R.string.first_name)) }
                )

                OutlinedTextField(
                    value = staffFormState.lastName,
                    onValueChange = {
                        staffFormState.onValueChange(
                            staffFormState.copy(
                                lastName = it
                            )
                        ) },
                    label = { Text(text = stringResource(R.string.last_name))}
                )

                // Bottom Dialog Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    // Cancellation Button
                    TextButton(
                        onClick = staffFormState.onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Confirmation Button
                    TextButton(
                        onClick = staffFormState.onConfirm,
                        enabled = staffFormState.isStaffDataValid
                    ) {
                        Text(
                            text = if(staffFormState.isStaffFormEditing)
                                stringResource(R.string.update) else stringResource(R.string.add),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
