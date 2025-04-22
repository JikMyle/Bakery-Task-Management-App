package com.example.bakerytaskmanagementapp.data.di

import android.content.Context
import androidx.room.Room
import com.example.bakerytaskmanagementapp.data.database.BTMDatabase
import com.example.bakerytaskmanagementapp.data.database.dao.InventoryItemDao
import com.example.bakerytaskmanagementapp.data.database.dao.StaffDao
import com.example.bakerytaskmanagementapp.data.database.dao.StaffTaskAssignmentDao
import com.example.bakerytaskmanagementapp.data.database.dao.TaskDao
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
}