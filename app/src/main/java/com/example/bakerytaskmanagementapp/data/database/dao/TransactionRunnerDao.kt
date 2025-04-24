package com.example.bakerytaskmanagementapp.data.database.dao

import androidx.room.RoomDatabase
import androidx.room.withTransaction
import javax.inject.Inject

class RoomTransactionRunner @Inject constructor(
    private val db: RoomDatabase
) : TransactionRunner {
    /**
     * Executes the given suspend [func] within a database transaction.
     */
    override suspend operator fun <T> invoke(func: suspend () -> T): T {
        return db.withTransaction(func)
    }
}

/**
 * Interface with operator function which will invoke the suspending lambda
 * within a database transaction.
 */
interface TransactionRunner {
    suspend operator fun <T> invoke(func: suspend () -> T): T
}