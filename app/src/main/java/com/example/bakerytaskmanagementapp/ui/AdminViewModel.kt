package com.example.bakerytaskmanagementapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.preferences.AdminDataStore
import com.example.bakerytaskmanagementapp.data.utils.EntryDialogCallbacks
import com.example.bakerytaskmanagementapp.data.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminDataStore: AdminDataStore
): ViewModel() {
    private var _adminUiState = MutableStateFlow(AdminUiState())
    val adminUiState = _adminUiState.asStateFlow()

    init {
        // The following code is temporary
        // Actual password should be set on initial launch by admin user
        // Might require a separate dialog or screen
        viewModelScope.launch {
            adminDataStore.changePassword("1234")
        }
    }

    fun toggleAdminMode() {
        if (adminUiState.value.isAdmin) {
            _adminUiState.update { it.copy(isAdmin = false) }
        } else {
            adminUiState.value.adminAccessDialogState?.let {
                enableAdminMode(it.password)
            }
        }
    }

    private fun enableAdminMode(password: String) {
        _adminUiState.update { it.copy(operationState = OperationState.Loading) }

        viewModelScope.launch {
            var isPasswordCorrect = false

            try {
                isPasswordCorrect = adminDataStore.checkIfPasswordMatch(password)
            } catch (e: Exception) {
                _adminUiState.update {
                    it.copy(
                        operationState =
                            OperationState.Error("Failed to check password: $e")
                    )
                }
            }

            if (!isPasswordCorrect) {
                _adminUiState.update {
                    it.copy(
                        operationState = OperationState.Error("Incorrect admin password.")
                    )
                }
                return@launch
            }

            try {
                _adminUiState.update {
                    it.copy(
                        isAdmin = isPasswordCorrect,
                        operationState = OperationState.Success("Admin mode enabled.")
                    )
                }
            } catch (e: Exception) {
                _adminUiState.update {
                    it.copy(
                        isAdmin = false,
                        operationState = OperationState.Error("Failed to enable admin mode: $e")
                    )
                }
            }
        }
    }

    private fun changePassword(newPassword: String) {
        _adminUiState.update { it.copy(operationState = OperationState.Loading) }

        viewModelScope.launch {
            try {
                adminDataStore.changePassword(newPassword)
                _adminUiState.update { it.copy(
                    operationState = OperationState.Success("Password changed successfully.")
                ) }
            } catch (e: Exception) {
                _adminUiState.update { it.copy(
                    operationState = OperationState.Error("Failed to change password: $e")
                ) }
            }
        }
    }

    fun resetOperationState() {
        _adminUiState.update { it.copy(operationState = OperationState.Idle) }
    }

    fun toggleAdminAccessDialogVisibility(isVisible: Boolean) {
        if(!isVisible) {
            _adminUiState.update {
                it.copy(
                    adminAccessDialogState = null
                )
            }
            return
        }

        _adminUiState.update {
            it.copy(
                adminAccessDialogState = AdminAccessDialogState(
                    dialogCallbacks = createAdminAccessDialogCallbacks()
                )
            )
        }
    }

    private fun createAdminAccessDialogCallbacks(): AdminDialogCallbacks {
        return AdminDialogCallbacks(
            onDismissClick = { toggleAdminAccessDialogVisibility(false) },
            onConfirmClick = {
                toggleAdminMode()
                toggleAdminAccessDialogVisibility(false)
            },
            onDeclineClick = { },
            onEntryDialogValueChange = { state ->
                updateAdminAccessDialogState(state)
            }
        )
    }

    private fun updateAdminAccessDialogState(state: AdminAccessDialogState) {
        _adminUiState.update { adminState ->
            adminState.copy(
                adminAccessDialogState = state.copy(
                    isDataValid = validateAdminAccessDialogState(state)
                )
            )
        }
    }

    private fun validateAdminAccessDialogState(state: AdminAccessDialogState): Boolean {
        return state.password.isNotBlank()
    }
}

data class AdminUiState(
    val isAdmin: Boolean = false,
    val operationState: OperationState = OperationState.Idle,
    val adminAccessDialogState: AdminAccessDialogState? = null,
    val changePasswordDialogState: ChangePasswordDialogState? = null
): UiState

data class AdminAccessDialogState(
    val password: String = "",
    val isDataValid: Boolean = false,
    val dialogCallbacks: AdminDialogCallbacks =
        AdminDialogCallbacks({}, {}, {}, {})
): UiState

data class ChangePasswordDialogState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isDataValid: Boolean = false,
    val dialogCallbacks: AdminDialogCallbacks =
        AdminDialogCallbacks({}, {}, {}, {})
)

class AdminDialogCallbacks(
    private val onDismissClick: () -> Unit,
    private val onConfirmClick: () -> Unit,
    private val onDeclineClick: () -> Unit,
    private val onEntryDialogValueChange: (AdminAccessDialogState) -> Unit
): EntryDialogCallbacks {
    override fun onDismiss() = onDismissClick()
    override fun onDecline() = onDeclineClick()

    override fun onConfirm() = onConfirmClick()
    override fun onValueChange(uiState: UiState) =
        onEntryDialogValueChange(uiState as AdminAccessDialogState)
}
