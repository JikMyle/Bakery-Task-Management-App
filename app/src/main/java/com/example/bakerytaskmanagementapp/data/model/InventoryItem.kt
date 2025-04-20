package com.example.bakerytaskmanagementapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_item"
)
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val stock: Int,
    @ColumnInfo(name = "date_created") val dateCreated: Long,
    @ColumnInfo(name = "date_last_updated") val dateLastUpdated: Long,
    @ColumnInfo(name = "date_expiration") val dateExpiration: Long?
)
