package com.example.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AuditLog
import com.example.data.model.DutyRecord
import com.example.data.model.PortalSettings
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.remote.FirebaseManager
import com.example.data.repository.AuditRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.DutyRepository
import com.example.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AdminUiState(
    val selectedTab: Int = 0, // 0: Overview, 1: Employees, 2: Portal URLs, 3: Audit Logs, 4: Cloud Sync
    val isSyncing: Boolean = false,
    val message: String? = null,
    val error: String? = null
)

class AdminViewModel(
    private val authRepository: AuthRepository,
    private val dutyRepository: DutyRepository,
    private val settingsRepository: SettingsRepository,
    private val auditRepository: AuditRepository,
    private val firebaseManager: FirebaseManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val currentUser: StateFlow<User?> = authRepository.currentUser
    val allUsers: StateFlow<List<User>> = authRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDuties: StateFlow<List<DutyRecord>> = dutyRepository.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val portalSettings: StateFlow<PortalSettings> = settingsRepository.getSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PortalSettings())

    val auditLogs: StateFlow<List<AuditLog>> = auditRepository.getRecentLogs(100)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectTab(tabIndex: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tabIndex, message = null, error = null)
    }

    fun updateUserRole(targetUser: User, newRole: UserRole, adminUser: User) {
        viewModelScope.launch {
            val updated = targetUser.copy(role = newRole)
            authRepository.updateUserProfile(updated)
            auditRepository.log(
                userId = adminUser.uid,
                userName = adminUser.fullName,
                action = "CHANGE_ROLE",
                details = "Changed role of ${targetUser.fullName} to ${newRole.name}"
            )
            _uiState.value = _uiState.value.copy(message = "Updated role of ${targetUser.fullName} to ${newRole.name}")
        }
    }

    fun updateSettings(settings: PortalSettings, adminUser: User) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
            auditRepository.log(
                userId = adminUser.uid,
                userName = adminUser.fullName,
                action = "UPDATE_SETTINGS",
                details = "Admin updated portal URLs and configurations"
            )
            _uiState.value = _uiState.value.copy(message = "Settings updated successfully.")
        }
    }

    fun triggerCloudSync(adminUser: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSyncing = true, message = null, error = null)
            val duties = allDuties.value
            var successCount = 0
            for (d in duties) {
                val ok = firebaseManager.syncDutyRecordToFirestore(d)
                if (ok) successCount++
            }
            auditRepository.log(
                userId = adminUser.uid,
                userName = adminUser.fullName,
                action = "CLOUD_SYNC",
                details = "Synced $successCount / ${duties.size} duty records with Firebase Firestore."
            )
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                message = "Synced $successCount records to Firebase Firestore."
            )
        }
    }
}
