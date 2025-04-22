package com.example.bakerytaskmanagementapp.data.database.repository

import com.example.bakerytaskmanagementapp.data.database.dao.InventoryItemDao
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import kotlinx.coroutines.flow.Flow

interface InventoryItemStore {
    fun getAllItems(): Flow<List<InventoryItem>>
    fun getItem(id: Int): Flow<InventoryItem?>

    /**
     * Adds inventory items to the database, can pass multiple [InventoryItem] instances
     */
    suspend fun addItem(vararg inventoryItem: InventoryItem)

    /**
     * Removes inventory items to the database, can pass multiple [InventoryItem] instances
     */
    suspend fun deleteItem(vararg inventoryItem: InventoryItem)

    /**
     * Updates inventory items to the database, can pass multiple [InventoryItem] instances
     */
    suspend fun updateItem(vararg inventoryItem: InventoryItem)
}

/**
 *  Local data repository for [InventoryItem] instances
 */
class LocalInventoryItemStore constructor(
    private val inventoryItemDao: InventoryItemDao
): InventoryItemStore {
    override fun getAllItems(): Flow<List<InventoryItem>> =
        inventoryItemDao.getAllItems()

    override fun getItem(id: Int): Flow<InventoryItem?> =
        inventoryItemDao.getItem(id)

    override suspend fun addItem(vararg inventoryItem: InventoryItem) {
        inventoryItemDao.addItem(*inventoryItem)
    }

    override suspend fun deleteItem(vararg inventoryItem: InventoryItem) {
        inventoryItemDao.deleteItem(*inventoryItem)
    }

    override suspend fun updateItem(vararg inventoryItem: InventoryItem) {
        inventoryItemDao.updateItem(*inventoryItem)
    }
}