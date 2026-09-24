package com.example.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val mobile: String = "",
    val depot: String = "",
    val depotCode: String = "",
    val designation: String = "",
    val dob: String = "",
    val photoUrl: String = "",
    val currentPass: String = "",
    val newPass: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class ProfileViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            currentUser.collect { u ->
                u?.let {
                    _uiState.value = _uiState.value.copy(
                        fullName = it.fullName,
                        mobile = it.mobile,
                        depot = it.depot,
                        depotCode = it.depotCode,
                        designation = it.designation,
                        dob = it.dob,
                        photoUrl = it.photoUrl
                    )
                }
            }
        }
    }

    fun onFullNameChange(v: String) { _uiState.value = _uiState.value.copy(fullName = v, errorMessage = null) }
    fun onMobileChange(v: String) { _uiState.value = _uiState.value.copy(mobile = v, errorMessage = null) }
    fun onDepotChange(v: String) { _uiState.value = _uiState.value.copy(depot = v, errorMessage = null) }
    fun onDepotCodeChange(v: String) { _uiState.value = _uiState.value.copy(depotCode = v, errorMessage = null) }
    fun onDesignationChange(v: String) { _uiState.value = _uiState.value.copy(designation = v, errorMessage = null) }
    fun onDobChange(v: String) { _uiState.value = _uiState.value.copy(dob = v, errorMessage = null) }
    fun onPhotoSelected(uri: String) { _uiState.value = _uiState.value.copy(photoUrl = uri, errorMessage = null) }

    fun onCurrentPassChange(v: String) { _uiState.value = _uiState.value.copy(currentPass = v, errorMessage = null) }
    fun onNewPassChange(v: String) { _uiState.value = _uiState.value.copy(newPass = v, errorMessage = null) }

    fun updateProfile(user: User) {
        val s = _uiState.value
        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            val updated = user.copy(
                fullName = s.fullName.trim(),
                mobile = s.mobile.trim(),
                depot = s.depot.trim(),
                depotCode = s.depotCode.trim(),
                designation = s.designation.trim(),
                dob = s.dob.trim(),
                photoUrl = s.photoUrl.trim()
            )
            val result = authRepository.updateUserProfile(updated)
            result.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Profile & photo updated successfully."
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to update profile."
                    )
                }
            )
        }
    }

    fun changePassword(user: User) {
        val s = _uiState.value
        if (s.newPass.length < 6) {
            _uiState.value = s.copy(errorMessage = "New password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = s.copy(isLoading = true, errorMessage = null)
            val result = authRepository.resetPassword(user.email, s.newPass)
            result.fold(
                onSuccess = { msg ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentPass = "",
                        newPass = "",
                        successMessage = msg
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to update password."
                    )
                }
            )
        }
    }
}
