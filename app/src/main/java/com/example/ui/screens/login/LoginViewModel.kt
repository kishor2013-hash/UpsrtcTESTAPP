package com.example.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val loginId: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val rememberMe: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isForgotPasswordDialogOpen: Boolean = false,
    val forgotPasswordInput: String = "",
    val forgotPasswordNewPass: String = "",
    val forgotPasswordMessage: String? = null,
    val forgotPasswordError: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val currentUser = authRepository.currentUser

    fun onLoginIdChange(value: String) {
        _uiState.value = _uiState.value.copy(loginId = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun togglePasswordVisibility() {
        _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.value = _uiState.value.copy(rememberMe = value)
    }

    fun login(onSuccess: (User) -> Unit) {
        val state = _uiState.value
        if (state.loginId.isBlank() || state.password.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Please enter both Login ID / Email and password.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.login(
                loginId = state.loginId,
                password = state.password,
                rememberMe = state.rememberMe
            )
            result.fold(
                onSuccess = { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Login successful. Welcome ${user.fullName}!"
                    )
                    onSuccess(user)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Login failed. Please check your credentials."
                    )
                }
            )
        }
    }

    fun openForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(
            isForgotPasswordDialogOpen = true,
            forgotPasswordInput = _uiState.value.loginId,
            forgotPasswordNewPass = "",
            forgotPasswordMessage = null,
            forgotPasswordError = null
        )
    }

    fun closeForgotPasswordDialog() {
        _uiState.value = _uiState.value.copy(isForgotPasswordDialogOpen = false)
    }

    fun onForgotPasswordInputChange(value: String) {
        _uiState.value = _uiState.value.copy(forgotPasswordInput = value, forgotPasswordError = null)
    }

    fun onForgotPasswordNewPassChange(value: String) {
        _uiState.value = _uiState.value.copy(forgotPasswordNewPass = value, forgotPasswordError = null)
    }

    fun resetPassword() {
        val state = _uiState.value
        if (state.forgotPasswordInput.isBlank()) {
            _uiState.value = state.copy(forgotPasswordError = "Please enter your Email or CND/Employee ID.")
            return
        }
        if (state.forgotPasswordNewPass.length < 6) {
            _uiState.value = state.copy(forgotPasswordError = "New password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            val result = authRepository.resetPassword(state.forgotPasswordInput, state.forgotPasswordNewPass)
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = _uiState.value.copy(
                        forgotPasswordMessage = msg,
                        forgotPasswordError = null
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        forgotPasswordError = err.localizedMessage ?: "Failed to reset password."
                    )
                }
            )
        }
    }
}
