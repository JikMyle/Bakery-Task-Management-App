package com.example.bakerytaskmanagementapp.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import com.example.bakerytaskmanagementapp.data.database.model.MeasurementUnit
import com.example.bakerytaskmanagementapp.data.database.repository.InventoryStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val inventoryStore: InventoryStore
): ViewModel() {
    private var _uiState = MutableStateFlow(InventoryState())
    val uiState = _uiState.asStateFlow()

    private var _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    init {
        loadInventoryItems()
    }

    private fun loadInventoryItems() {
        viewModelScope.launch {
            inventoryStore.getAllItems().collect { items ->
                _uiState.update {
                    it.copy(items = items)
                }
            }
        }
    }

    private fun insertItemToDatabase(item: InventoryItem) {
        viewModelScope.launch {
            try {
                inventoryStore.addItem(item)
                val message = "Item added successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error adding item: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    private fun updateItemInDatabase(item: InventoryItem) {
        viewModelScope.launch {
            try {
                inventoryStore.updateItem(item)
                val message = "Item updated successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error updating item: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    private fun deleteItemFromDatabase(item: InventoryItem) {
        viewModelScope.launch {
            try {
                inventoryStore.deleteItem(item)
                val message = "Item deleted successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error deleting item: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }

    fun toggleItemFormVisibility(isVisible: Boolean) {
        _uiState.update {
            it.copy(isItemFormVisible = isVisible)
        }

        if (!isVisible) updateItemFormState(InventoryItemFormState())
    }

    fun editItem(item: InventoryItem) {
        updateItemFormState(
            InventoryItemFormState(
                id = item.id.toString(),
                name = item.name,
                quantity = item.stock.toString(),
                unit = item.unit.unit,
                isEditing = true
            )
        )

        toggleItemFormVisibility(true)
    }

    fun deleteItem(item: InventoryItem) {
        deleteItemFromDatabase(item)
    }

    fun saveItem() {
        val item = InventoryItem(
            id = uiState.value.itemFormState.id.toInt(),
            name = uiState.value.itemFormState.name,
            stock = uiState.value.itemFormState.quantity.toFloat(),
            unit = MeasurementUnit.entries.find {
                it.unit == uiState.value.itemFormState.unit
            } ?: MeasurementUnit.PIECE
        )

        toggleItemFormVisibility(false)

        if (uiState.value.itemFormState.isEditing) {
            updateItemInDatabase(item)
        } else {
            insertItemToDatabase(item)
        }
    }

    fun updateItemFormState(
        itemFormState: InventoryItemFormState = _uiState.value.itemFormState
    ) {
        val formState = itemFormState.copy(
            isDataValid = validateItemForm(itemFormState)
        )

        _uiState.update {
            it.copy(
                itemFormState = formState
            )
        }
    }

    private fun validateItemForm(
        itemFormState: InventoryItemFormState
    ): Boolean {
        val isQuantityValid = itemFormState.quantity.toFloatOrNull()?.let {
            it >= 0
        } ?: false

        return !(itemFormState.name.isBlank()
                or !isQuantityValid
                or MeasurementUnit.entries.none { it.unit == itemFormState.unit })
    }
}

data class InventoryState(
    val items: List<InventoryItem> = emptyList(),
    val isItemFormVisible: Boolean = false,
    val itemFormState: InventoryItemFormState = InventoryItemFormState(),
)

data class InventoryItemFormState(
    val id: String = "0",
    val name: String = "",
    val quantity: String = "",
    val unit: String = "",
    val isEditing: Boolean = false,
    val isDataValid: Boolean = false,
    val isUnitMenuExpanded: Boolean = false,
    val unitMap: Map<String, String> = MeasurementUnit.entries.associate { it.name to it.unit }
)