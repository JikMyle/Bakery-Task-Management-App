package com.example.bakerytaskmanagementapp.data.utils

interface UiState

interface DialogCallbacks {
    fun onDismiss()
    fun onDecline()
    fun onConfirm()
}

interface EntryDialogCallbacks: DialogCallbacks {
    override fun onDismiss()
    override fun onDecline()
    override fun onConfirm()
    fun onValueChange(uiState: UiState)
}