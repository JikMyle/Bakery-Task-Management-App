package com.example.bakerytaskmanagementapp.ui.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bakerytaskmanagementapp.data.database.model.Staff
import com.example.bakerytaskmanagementapp.data.database.repository.StaffStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffStore: StaffStore
): ViewModel() {
    private var _staffScreenState = MutableStateFlow(StaffScreenState())
    val uiState = _staffScreenState as StateFlow<StaffScreenState>

    init {
        // Fetch staff from the database, updates UI state on updates in database
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

    private suspend fun addStaff(staff: Staff) {
        staffStore.addStaff(staff)
    }

    private suspend fun updateStaff(staff: Staff) {
        staffStore.updateStaff(staff)
    }

    private suspend fun deleteStaff(staff: Staff) {
        staffStore.deleteStaff(staff)
    }

    fun toggleStaffFormVisibility() {
        _staffScreenState.update {
            it.copy(
                isStaffFormVisible = !it.isStaffFormVisible
            )
        }
    }

    /**
     * Updates UI State with new State instance
     * Function can be used as middleware for other function such as validation
     */
    private fun updateUiState(uiState: StaffScreenState = _staffScreenState.value) {
        _staffScreenState.value = uiState
    }

    /**
     * Updates Staff Form State with new State instance
     * Function can be used as middleware for other function such as form validation
     */
    fun updateStaffFormState(
        staffFormState: StaffFormState = _staffScreenState.value.staffFormState
    ) {
        val formState = uiState.value.staffFormState.copy(
            isStaffDataValid = validateStaffForm(staffFormState)
        )

        updateUiState(
            uiState.value.copy(
                staffFormState = formState
            )
        )
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
    val staffFormState: StaffFormState = StaffFormState()
)

data class StaffFormState(
    val firstName: String = "",
    val lastName: String = "",
    val isStaffFormEditing: Boolean = false,
    val isStaffDataValid: Boolean = false,
    val onPositiveClick: () -> Unit = {},
    val onNegativeClick: () -> Unit = {},
)