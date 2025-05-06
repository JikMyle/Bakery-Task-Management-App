package com.example.bakerytaskmanagementapp.ui.inventory

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bakerytaskmanagementapp.R
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import com.example.bakerytaskmanagementapp.data.database.model.MeasurementUnit

@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    viewModel: InventoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
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

    if(uiState.value.isItemFormVisible) {
        ItemFormDialog(
            itemFormState = uiState.value.itemFormState,
            onValueChange = viewModel::updateItemFormState,
            onConfirm = viewModel::saveItem,
            onDismiss = {
                viewModel.toggleItemFormVisibility(false)
            })
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
                text = stringResource(R.string.manage_inventory),
                fontSize = MaterialTheme.typography.displayMedium.fontSize,
                fontWeight = FontWeight.Bold
            )

            AddItemButton(
                onClick = { viewModel.toggleItemFormVisibility(true) }
            )
        }

        InventoryList (
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxSize(),
            itemList = uiState.value.items,
            onItemDeleteClick = viewModel::deleteItem,
            onItemEditClick = viewModel::editItem
        )
    }
}

@Composable
private fun InventoryList(
    modifier: Modifier = Modifier,
    itemList: List<InventoryItem>,
    onItemEditClick: (InventoryItem) -> Unit,
    onItemDeleteClick: (InventoryItem) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp)
    ) {
        items(items = itemList) { item ->
            InventoryListItem (
                modifier = Modifier,
                item = item,
                onItemEditClick = onItemEditClick,
                onItemDeleteClick = onItemDeleteClick
            )
        }
    }
}

@Composable
private fun InventoryListItem(
    modifier: Modifier = Modifier,
    item: InventoryItem,
    onItemEditClick: (InventoryItem) -> Unit,
    onItemDeleteClick: (InventoryItem) -> Unit,
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
            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )

                Text(
                    text = "${
                        if(item.unit == MeasurementUnit.PIECE) {
                            item.stock.toInt().toString()
                        } else {
                            item.stock.toString()
                        }
                    } ${item.unit.unit}",
                    style = MaterialTheme.typography.labelLarge,
                )
            }

            // This [SPACER] is to separate the left and right contents
            Spacer(modifier.weight(1f))

            IconButton(
                onClick = { onItemEditClick(item) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription =
                        stringResource(R.string.edit_item_button_content_description)
                )
            }

            IconButton(
                onClick = { onItemDeleteClick(item) }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription =
                        stringResource(R.string.delete_item_button_content_description)
                )
            }
        }
    }
}

@Composable
private fun ItemFormDialog(
    modifier: Modifier = Modifier,
    itemFormState: InventoryItemFormState,
    onValueChange: (InventoryItemFormState) -> Unit = {},
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(modifier = modifier.width(300.dp)) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = if(itemFormState.isEditing) stringResource(R.string.edit_item)
                    else stringResource(R.string.add_item),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = itemFormState.name,
                    onValueChange = {
                        onValueChange(
                            itemFormState.copy(
                                name = it
                            )
                        ) },
                    label = { Text(text = stringResource(R.string.item_name)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next),
                )

                OutlinedTextField(
                    value = itemFormState.quantity,
                    onValueChange = {
                        onValueChange(
                            itemFormState.copy(
                                quantity = it
                            )
                        ) },
                    label = { Text(text = stringResource(R.string.quantity)) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next)
                )

                MeasurementUnitField(
                    selectedUnit = itemFormState.unit,
                    unitMap = itemFormState.unitMap,
                    isUnitMenuVisible = itemFormState.isUnitMenuExpanded,
                    onMenuItemClick = {
                        onValueChange(itemFormState.copy(
                            unit = it,
                            isUnitMenuExpanded = false
                        ))
                    },
                    onToggleMenuVisibility = {
                        onValueChange(itemFormState.copy(isUnitMenuExpanded = it))
                    }
                )

                // Bottom Dialog Buttons
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End
                ) {

                    // Cancellation Button
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Confirmation Button
                    TextButton(
                        onClick = onConfirm,
                        enabled = itemFormState.isDataValid
                    ) {
                        Text(
                            text = if(itemFormState.isEditing)
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
private fun MeasurementUnitField(
    modifier: Modifier = Modifier,
    selectedUnit: String,
    unitMap: Map<String, String>,
    isUnitMenuVisible: Boolean,
    onMenuItemClick: (String) -> Unit,
    onToggleMenuVisibility: (Boolean) -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = isUnitMenuVisible,
        onExpandedChange = onToggleMenuVisibility,
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = selectedUnit,
            onValueChange = { },
            label = {
                Text(text = stringResource(R.string.measurement_unit))
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isUnitMenuVisible,
                    modifier = Modifier.menuAnchor(MenuAnchorType.SecondaryEditable)
                )
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryEditable),
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )

        ExposedDropdownMenu(
            expanded = isUnitMenuVisible,
            onDismissRequest = { onToggleMenuVisibility(false) },
        ) {
            for ((name, unit) in unitMap) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${name.lowercase().replaceFirstChar { it.uppercaseChar() }} ($unit)")
                           },
                    onClick = { onMenuItemClick(unit) },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
private fun AddItemButton(
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
                text = stringResource(R.string.add_item),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )

            Icon(
                modifier = Modifier.padding(start = 8.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(
                    R.string.add_item_button_content_description
                )
            )
        }
    }
}