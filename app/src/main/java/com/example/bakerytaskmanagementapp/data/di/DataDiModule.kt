package com.example.bakerytaskmanagementapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.bakerytaskmanagementapp.data.database.BTMDatabase
import com.example.bakerytaskmanagementapp.data.database.dao.InventoryItemDao
import com.example.bakerytaskmanagementapp.data.database.dao.RoomTransactionRunner
import com.example.bakerytaskmanagementapp.data.database.dao.StaffDao
import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskHistoryDao
import com.example.bakerytaskmanagementapp.data.database.dao.TransactionRunner
import com.example.bakerytaskmanagementapp.data.database.repository.InventoryItemStore
import com.example.bakerytaskmanagementapp.data.database.repository.LocalInventoryItemStore
import com.example.bakerytaskmanagementapp.data.database.repository.LocalStaffStore
import com.example.bakerytaskmanagementapp.data.database.repository.LocalTaskHistoryStore
import com.example.bakerytaskmanagementapp.data.database.repository.LocalTaskStore
import com.example.bakerytaskmanagementapp.data.database.repository.StaffStore
import com.example.bakerytaskmanagementapp.data.database.repository.TaskHistoryStore
import com.example.bakerytaskmanagementapp.data.database.repository.TaskStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataDiModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): BTMDatabase =
        Room.databaseBuilder(context, BTMDatabase::class.java, "btm_database")
            .createFromAsset("btm_database.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun provideTaskDao(
        database: BTMDatabase
    ): TaskDao = database.taskDao()

    @Provides
    @Singleton
    fun provideStaffDao(
        database: BTMDatabase
    ): StaffDao = database.staffDao()

    @Provides
    @Singleton
    fun provideStaffTaskAssignmentDao(
        database: BTMDatabase
    ): StaffTaskAssignmentDao = database.staffTaskAssignmentDao()

    @Provides
    @Singleton
    fun provideInventoryItemDao(
        database: BTMDatabase
    ): InventoryItemDao = database.inventoryItemDao()

    @Provides
    @Singleton
    fun provideTaskHistoryDao(
        database: BTMDatabase
    ): TaskHistoryDao = database.taskHistoryDao()

    @Provides
    @Singleton
    fun provideTransactionRunner(
        db: BTMDatabase
    ): TransactionRunner = RoomTransactionRunner(db)

    @Provides
    @Singleton
    fun provideTaskStore(
        taskDao: TaskDao,
        staffTaskAssignmentDao: StaffTaskAssignmentDao,
        taskHistoryDao: TaskHistoryDao,
        transactionRunner: TransactionRunner,
    ): TaskStore = LocalTaskStore(
        taskDao,
        staffTaskAssignmentDao,
        taskHistoryDao,
        transactionRunner
    )

    @Provides
    @Singleton
    fun provideStaffStore(
        staffDao: StaffDao
    ): StaffStore = LocalStaffStore(
        staffDao,
    )

    @Provides
    @Singleton
    fun provideInventoryItemStore(
        inventoryItemDao: InventoryItemDao
    ): InventoryItemStore = LocalInventoryItemStore(
        inventoryItemDao
    )

    @Provides
    @Singleton
    fun provideTaskHistoryStore(
        taskDao: TaskDao,
        taskHistoryDao: TaskHistoryDao,
        transactionRunner: TransactionRunner
    ): TaskHistoryStore = LocalTaskHistoryStore(
        taskDao,
        taskHistoryDao,
        transactionRunner
    )
}