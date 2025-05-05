package com.example.bakerytaskmanagementapp.ui.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.model.Staff


@Composable
fun StaffScreen(
    modifier: Modifier = Modifier,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

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
        Text(
            modifier = Modifier,
            text = stringResource(R.string.manage_staff),
            fontSize = MaterialTheme.typography.displayMedium.fontSize,
            fontWeight = FontWeight.Bold
        )

        AddStaffButton(onClick = {
            viewModel.updateStaffFormState(StaffFormState())
            viewModel.toggleStaffFormVisibility()
        })

        StaffList(
            modifier = Modifier.padding(top = 6.dp),
            staffList = uiState.value.staff,
            onItemDeleteClick = {},
            onItemEditClick = {}
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                style = MaterialTheme.typography.headlineSmall
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

@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    letter: String,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraLarge
            ),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = letter,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun StaffFormDialog(
    modifier: Modifier = Modifier,
    staffFormState: StaffFormState
) {
    Dialog(
        onDismissRequest = { }
    ) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Add Staff",
                    style = MaterialTheme.typography.titleLarge,
                )

                OutlinedTextField(
                    value = staffFormState.firstName,
                    onValueChange = { staffFormState.onValueChange() },
                    label = { Text(text = "First Name") }
                )

                OutlinedTextField(
                    value = staffFormState.lastName,
                    onValueChange = { staffFormState.onValueChange() },
                    label = { Text(text = "Last Name")}
                )
                
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    TextButton(
                        onClick = { staffFormState.onDismiss() }
                    ) {
                        Text(
                            text = "Cancel",
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = { staffFormState.onConfirm() }
                    ) {
                        Text(text = "Confirm")
                    }
                }
            }
        }
    }
}
