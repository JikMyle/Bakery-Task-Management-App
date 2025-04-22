package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem

@Dao
interface InventoryItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(vararg inventoryItem: InventoryItem)

    @Delete
    fun deleteItem(vararg inventoryItem: InventoryItem)

    @Update
    fun updateItem(vararg inventoryItem: InventoryItem)

    @Query("SELECT * FROM inventory_item")
    fun getAllItems(): List<InventoryItem>

    @Query("SELECT * FROM inventory_item WHERE id = :id")
    fun getItem(id: Int): InventoryItem
}