package com.example.bakerytaskmanagementapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bakerytaskmanagementapp.data.database.dao.InventoryItemDao
import com.example.bakerytaskmanagementapp.data.database.dao.StaffDao
import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
import com.example.bakerytaskmanagementapp.data.database.model.InventoryItem
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.model.StaffTaskAssignment
import com.example.bakerytaskmanagementapp.data.database.model.Task

@Database(
    entities = [
        Task::class,
        Staff::class,
        StaffTaskAssignment::class,
        InventoryItem::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BTMDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun staffDao(): StaffDao
    abstract fun staffTaskAssignmentDao(): StaffTaskAssignmentDao
    abstract fun inventoryItemDao(): InventoryItemDao
}