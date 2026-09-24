package com.example.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.EmployeeType
import com.example.data.repository.AuthRepository
import com.example.data.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    // Personal info
    val fullName: String = "",
    val dob: String = "",
    val mobile: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    // Employee info
    val employeeId: String = "",
    val employeeType: EmployeeType = EmployeeType.CONDUCTOR,
    // Depot info
    val depot: String = "SOHRAB GATE DEPOT",
    val depotCode: String = "SGB",
    // Other
    val designation: String = "Conductor",
    // State
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isRegistrationComplete: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) { _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null) }
    fun onDobChange(value: String) { _uiState.value = _uiState.value.copy(dob = value, errorMessage = null) }
    fun onMobileChange(value: String) { _uiState.value = _uiState.value.copy(mobile = value, errorMessage = null) }
    fun onEmailChange(value: String) { _uiState.value = _uiState.value.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) { _uiState.value = _uiState.value.copy(password = value, errorMessage = null) }
    fun onConfirmPasswordChange(value: String) { _uiState.value = _uiState.value.copy(confirmPassword = value, errorMessage = null) }
    fun onEmployeeIdChange(value: String) { _uiState.value = _uiState.value.copy(employeeId = value, errorMessage = null) }
    fun onEmployeeTypeChange(value: EmployeeType) {
        val defaultDesig = if (value == EmployeeType.DRIVER) "Bus Driver" else "Bus Conductor"
        _uiState.value = _uiState.value.copy(employeeType = value, designation = defaultDesig, errorMessage = null)
    }
    fun onDepotChange(value: String) { _uiState.value = _uiState.value.copy(depot = value, errorMessage = null) }
    fun onDepotCodeChange(value: String) { _uiState.value = _uiState.value.copy(depotCode = value, errorMessage = null) }
    fun onDesignationChange(value: String) { _uiState.value = _uiState.value.copy(designation = value, errorMessage = null) }

    fun register(onSuccess: () -> Unit) {
        val s = _uiState.value

        // Form validations
        if (s.fullName.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please enter your Full Name.")
            return
        }
        if (!SecurityUtils.isValidMobile(s.mobile)) {
            _uiState.value = s.copy(errorMessage = "Please enter a valid 10-digit mobile number.")
            return
        }
        if (!SecurityUtils.isValidEmail(s.email)) {
            _uiState.value = s.copy(errorMessage = "Please enter a valid email address.")
            return
        }
        if (!SecurityUtils.isValidEmployeeId(s.employeeId)) {
            _uiState.value = s.copy(errorMessage = "Please enter a valid Employee ID / CND.")
            return
        }
        if (s.password.length < 6) {
            _uiState.value = s.copy(errorMessage = "Password must be at least 6 characters.")
            return
        }
        if (s.password != s.confirmPassword) {
            _uiState.value = s.copy(errorMessage = "Passwords do not match.")
            return
        }
        if (s.depot.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please specify your Depot.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.register(
                fullName = s.fullName,
                dob = s.dob,
                mobile = s.mobile,
                email = s.email,
                password = s.password,
                employeeId = s.employeeId,
                employeeType = s.employeeType,
                depot = s.depot,
                depotCode = s.depotCode,
                designation = s.designation
            )
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegistrationComplete = true,
                        successMessage = "Account created successfully. Please login to continue."
                    )
                    onSuccess()
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Registration failed."
                    )
                }
            )
        }
    }
}
