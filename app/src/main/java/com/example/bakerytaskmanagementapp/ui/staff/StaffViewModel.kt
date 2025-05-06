package com.example.bakerytaskmanagementapp.ui.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.OperationState
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.repository.StaffStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffStore: StaffStore
): ViewModel() {
    private var _staffScreenState = MutableStateFlow(StaffScreenState())
    val uiState = _staffScreenState.asStateFlow()

    private var _operationState = MutableStateFlow<OperationState>(OperationState.Idle)
    val operationState = _operationState.asStateFlow()

    init {
        loadStaff()
    }

    // Fetch staff from the database, updates UI state on updates in database
    private fun loadStaff() {
        viewModelScope.launch {
            staffStore.getAllStaff().collect { staff ->
                _staffScreenState.update {
                    it.copy(
                        staff = staff
                    )
                }
            }
        }
    }

    private fun setupStaffFormHandling() {
        val staffFormState = _staffScreenState.value.staffFormState.copy(
            onValueChange = ::updateStaffFormState,
            onConfirm = {
                val staff = Staff(
                    id = uiState.value.staffFormState.id,
                    firstName = uiState.value.staffFormState.firstName,
                    lastName = uiState.value.staffFormState.lastName,
                    profilePath = null
                )

                if(uiState.value.staffFormState.isEditing) {
                    updateStaffInDatabase(staff)
                } else {
                    insertStaffToDatabase(staff)
                }

                toggleStaffFormVisibility(false)
            },
            onDismiss = { toggleStaffFormVisibility(false) }
        )

        updateStaffFormState(staffFormState)
    }

    private fun insertStaffToDatabase(staff: Staff) {
        viewModelScope.launch {
            try {
                staffStore.addStaff(staff)
                val message = "Staff added successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error adding staff: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    private fun updateStaffInDatabase(staff: Staff) {
        viewModelScope.launch {
            try {
                staffStore.updateStaff(staff)
                val message = "Staff updated successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error updating staff: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    private fun deleteStaffFromDatabase(staff: Staff) {
        viewModelScope.launch {
            try {
                staffStore.deleteStaff(staff)
                val message = "Staff deleted successfully"
                _operationState.update { OperationState.Success(message) }
            } catch (e: Exception) {
                val message = "Error deleting staff: ${e.message}"
                _operationState.update { OperationState.Error(message) }
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = OperationState.Idle
    }

    fun editStaff(staff: Staff) {
        updateStaffFormState(
            StaffFormState(
                id = staff.id,
                firstName = staff.firstName,
                lastName = staff.lastName,
                isEditing = true
            )
        )
        toggleStaffFormVisibility(true)
    }

    fun deleteStaff(staff: Staff) {
        deleteStaffFromDatabase(staff)
    }

    fun clearStaffForm() {
        updateStaffFormState(StaffFormState())
    }

    fun toggleStaffFormVisibility(isVisible: Boolean) {
        if(isVisible) {
            setupStaffFormHandling()
        }

        _staffScreenState.update {
            it.copy(
                isStaffFormVisible = isVisible
            )
        }

        if(!isVisible) {
            clearStaffForm()
        }
    }

    /**
     * Updates UI State with new State instance
     * Function can be used as middleware for other function such as validation
     */
    private fun updateUiState(uiState: StaffScreenState = _staffScreenState.value) {
        _staffScreenState.update {
            uiState
        }
    }

    /**
     * Updates Staff Form State with new State instance
     * Function can be used as middleware for other function such as form validation
     */
    private fun updateStaffFormState(
        staffFormState: StaffFormState = _staffScreenState.value.staffFormState
    ) {
        val formState = staffFormState.copy(
            isDataValid = validateStaffForm(staffFormState)
        )

        _staffScreenState.update {
            it.copy(
                staffFormState = formState
            )
        }
    }

    /**
     * Validates staff form data, returns true if data is valid
     */
    private fun validateStaffForm(staffFormState: StaffFormState): Boolean {
        return !(staffFormState.firstName.isBlank()
                or staffFormState.lastName.isBlank())
    }
}

data class StaffScreenState(
    val staff: List<Staff> = emptyList(),
    val isStaffFormVisible: Boolean = false,
    val staffFormState: StaffFormState = StaffFormState(),
)

data class StaffFormState(
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val isEditing: Boolean = false,
    val isDataValid: Boolean = false,
    val onValueChange: (StaffFormState) -> Unit = {},
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {},
)