package com.example.bakerytaskmanagementapp.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "inventory_item"
)
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val stock: Float,
    val unit: MeasurementUnit,
    @ColumnInfo(name = "date_created") val dateCreated: Date = Date(),
    @ColumnInfo(name = "date_last_updated") val dateLastUpdated: Date = Date(),
    @ColumnInfo(name = "date_expiration") val dateExpiration: Date? = null,
)

enum class MeasurementUnit(val unit: String) {
    KILOGRAM("kg"),
    GRAM("g"),
    PIECE("pcs"),
    LITER("l"),
    MILLILITER("ml"),
}
