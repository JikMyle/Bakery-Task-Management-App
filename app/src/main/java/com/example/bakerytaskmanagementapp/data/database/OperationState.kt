package com.example.bakerytaskmanagementapp.data.database

/**
 * State classes for database operations
 */
sealed class OperationState {
    data object Idle: OperationState()
    data object Loading: OperationState()
    data class Success(val message: String): OperationState()
    data class Error(val message: String): OperationState()

}