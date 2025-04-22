package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryItemStore {
    fun getAllItems(): Flow<List<InventoryItem>>
    suspend fun getItem(id: Int): InventoryItem?

    suspend fun addItem(vararg inventoryItem: InventoryItem)
    suspend fun deleteItem(vararg inventoryItem: InventoryItem)
    suspend fun updateItem(vararg inventoryItem: InventoryItem)
}