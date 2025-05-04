package com.example.bakerytaskmanagementapp.ui.staff

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

    StaffScreenContent()
}

@Composable
private fun StaffScreenContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(bottom = 18.dp),
            text = stringResource(R.string.manage_staff),
            fontSize = MaterialTheme.typography.displayMedium.fontSize,
            fontWeight = FontWeight.Bold
        )

        AddStaffButton()
        StaffList()
    }
}

@Composable
private fun AddStaffButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
) {
    OutlinedButton(
        shape = MaterialTheme.shapes.medium,
        onClick = { /*TODO*/ }
    ) {
        Row(
            modifier = Modifier.padding(24.dp, 12.dp),
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
    staffList: List<Staff> = emptyList(),
    onItemEditClick: (Staff) -> Unit = {},
    onItemDeleteClick: (Staff) -> Unit = {},
) {
    LazyColumn(modifier = modifier) {
        items(items = staffList) { staff ->
            Card {
                Text(
                    text = "${staff.firstName} ${staff.lastName}",
                )

                // This [SPACER] is to separate the left and right contents
                Spacer(modifier.fillMaxWidth())
                
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
}

@Preview(showBackground = true,device = "id:medium_tablet")
@Composable
fun PreviewStaffScreen() {
    StaffScreenContent()
}