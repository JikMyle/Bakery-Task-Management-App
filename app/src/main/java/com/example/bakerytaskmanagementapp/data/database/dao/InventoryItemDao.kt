package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addItem(vararg inventoryItem: InventoryItem)

    @Delete
    suspend fun deleteItem(vararg inventoryItem: InventoryItem)

    @Update
    suspend fun updateItem(vararg inventoryItem: InventoryItem)

    @Query("SELECT * FROM inventory_item")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_item WHERE id = :id")
    suspend fun getItem(id: Int): InventoryItem
}