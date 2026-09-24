package com.example.ui.screens.feedduty

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DutyRecord
import com.example.data.model.DutyStatus
import com.example.data.model.User
import com.example.data.repository.AuthRepository
import com.example.data.repository.DutyRepository
import com.example.data.util.SecurityUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FeedDutyUiState(
    // Duty fields
    val dutyDate: String = "",
    val dutyNumber: String = "",
    val busNumber: String = "",
    val busRegistrationNumber: String = "",
    val route: String = "",
    val routeNumber: String = "",
    val startPoint: String = "",
    val endPoint: String = "",
    val dutyStartTime: String = "06:00",
    val dutyEndTime: String = "14:30",
    val shift: String = "Morning",
    // Operational data
    val totalKm: String = "",
    val income: String = "",
    val passengerCount: String = "",
    val loadFactor: String = "",
    val numberOfTrips: String = "2",
    val ticketCollection: String = "",
    val cashCollection: String = "",
    val remarks: String = "",
    // Flow states
    val isLoading: Boolean = false,
    val showConfirmSubmitDialog: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val submittedRecordId: String? = null
)

class FeedDutyViewModel(
    private val authRepository: AuthRepository,
    private val dutyRepository: DutyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedDutyUiState())
    val uiState: StateFlow<FeedDutyUiState> = _uiState.asStateFlow()

    val currentUser = authRepository.currentUser

    init {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        _uiState.value = _uiState.value.copy(dutyDate = dateFormat.format(Date()))
    }

    fun onDutyDateChange(v: String) { _uiState.value = _uiState.value.copy(dutyDate = v, errorMessage = null) }
    fun onDutyNumberChange(v: String) { _uiState.value = _uiState.value.copy(dutyNumber = v, errorMessage = null) }
    fun onBusNumberChange(v: String) { _uiState.value = _uiState.value.copy(busNumber = v, errorMessage = null) }
    fun onBusRegistrationChange(v: String) { _uiState.value = _uiState.value.copy(busRegistrationNumber = v) }
    fun onRouteChange(v: String) {
        _uiState.value = _uiState.value.copy(route = v, errorMessage = null)
        // If route has " - ", split to startPoint and endPoint
        if (v.contains("-")) {
            val parts = v.split("-")
            if (parts.size >= 2) {
                _uiState.value = _uiState.value.copy(
                    startPoint = parts[0].trim(),
                    endPoint = parts[1].trim()
                )
            }
        }
    }
    fun onRouteNumberChange(v: String) { _uiState.value = _uiState.value.copy(routeNumber = v) }
    fun onStartPointChange(v: String) { _uiState.value = _uiState.value.copy(startPoint = v) }
    fun onEndPointChange(v: String) { _uiState.value = _uiState.value.copy(endPoint = v) }
    fun onDutyStartTimeChange(v: String) { _uiState.value = _uiState.value.copy(dutyStartTime = v) }
    fun onDutyEndTimeChange(v: String) { _uiState.value = _uiState.value.copy(dutyEndTime = v) }
    fun onShiftChange(v: String) { _uiState.value = _uiState.value.copy(shift = v) }

    fun onTotalKmChange(v: String) {
        _uiState.value = _uiState.value.copy(totalKm = v, errorMessage = null)
        autoCalculateLoadFactor()
    }

    fun onIncomeChange(v: String) {
        _uiState.value = _uiState.value.copy(
            income = v,
            ticketCollection = v,
            cashCollection = v,
            errorMessage = null
        )
    }

    fun onPassengerCountChange(v: String) {
        _uiState.value = _uiState.value.copy(passengerCount = v)
        autoCalculateLoadFactor()
    }

    fun onLoadFactorChange(v: String) { _uiState.value = _uiState.value.copy(loadFactor = v) }
    fun onNumberOfTripsChange(v: String) { _uiState.value = _uiState.value.copy(numberOfTrips = v) }
    fun onTicketCollectionChange(v: String) { _uiState.value = _uiState.value.copy(ticketCollection = v) }
    fun onCashCollectionChange(v: String) { _uiState.value = _uiState.value.copy(cashCollection = v) }
    fun onRemarksChange(v: String) { _uiState.value = _uiState.value.copy(remarks = v) }

    private fun autoCalculateLoadFactor() {
        val passengers = _uiState.value.passengerCount.toIntOrNull() ?: 0
        val trips = _uiState.value.numberOfTrips.toIntOrNull() ?: 1
        val capacityPerTrip = 52.0 // Standard UPSRTC bus seating capacity
        val totalCapacity = capacityPerTrip * trips
        if (totalCapacity > 0 && passengers > 0) {
            val factor = ((passengers / totalCapacity) * 100).coerceAtMost(100.0)
            _uiState.value = _uiState.value.copy(loadFactor = "%.1f".format(factor))
        }
    }

    fun promptConfirmSubmit() {
        val s = _uiState.value
        if (s.dutyDate.isBlank() || s.dutyNumber.isBlank() || s.busNumber.isBlank() || s.route.isBlank() || s.totalKm.isBlank() || s.income.isBlank()) {
            _uiState.value = s.copy(errorMessage = "Please fill in all required duty fields (Date, Duty #, Bus #, Route, KM, Income).")
            return
        }
        _uiState.value = s.copy(showConfirmSubmitDialog = true, errorMessage = null)
    }

    fun dismissConfirmDialog() {
        _uiState.value = _uiState.value.copy(showConfirmSubmitDialog = false)
    }

    fun submitDuty(user: User, onSuccess: (String) -> Unit) {
        _uiState.value = _uiState.value.copy(showConfirmSubmitDialog = false, isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val s = _uiState.value
            val recordId = SecurityUtils.generateRecordId(user.employeeType.name)
            val km = s.totalKm.toDoubleOrNull() ?: 0.0
            val inc = s.income.toDoubleOrNull() ?: 0.0
            val pass = s.passengerCount.toIntOrNull() ?: 0
            val lf = s.loadFactor.toDoubleOrNull() ?: 0.0
            val trips = s.numberOfTrips.toIntOrNull() ?: 1
            val tickets = s.ticketCollection.toDoubleOrNull() ?: inc
            val cash = s.cashCollection.toDoubleOrNull() ?: inc

            val record = DutyRecord(
                recordId = recordId,
                userId = user.uid,
                employeeId = user.employeeId,
                employeeName = user.fullName,
                employeeType = user.employeeType.name,
                depot = user.depot,
                dutyDate = s.dutyDate,
                dutyNumber = s.dutyNumber.trim().uppercase(),
                busNumber = s.busNumber.trim().uppercase(),
                busRegistrationNumber = s.busRegistrationNumber.trim().uppercase(),
                route = s.route.trim(),
                routeNumber = s.routeNumber.trim(),
                startPoint = s.startPoint.trim(),
                endPoint = s.endPoint.trim(),
                dutyStartTime = s.dutyStartTime,
                dutyEndTime = s.dutyEndTime,
                shift = s.shift,
                totalKm = km,
                income = inc,
                passengerCount = pass,
                loadFactor = lf,
                numberOfTrips = trips,
                ticketCollection = tickets,
                cashCollection = cash,
                remarks = s.remarks.trim(),
                status = DutyStatus.SUBMITTED
            )

            val result = dutyRepository.submitDuty(record)
            result.fold(
                onSuccess = { saved ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submittedRecordId = saved.recordId,
                        successMessage = "Duty record submitted successfully! Record ID: ${saved.recordId}"
                    )
                    onSuccess(saved.recordId)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to submit duty record."
                    )
                }
            )
        }
    }

    fun saveDraft(user: User, onSuccess: (String) -> Unit) {
        val s = _uiState.value
        _uiState.value = s.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val recordId = SecurityUtils.generateRecordId(user.employeeType.name)
            val km = s.totalKm.toDoubleOrNull() ?: 0.0
            val inc = s.income.toDoubleOrNull() ?: 0.0

            val record = DutyRecord(
                recordId = recordId,
                userId = user.uid,
                employeeId = user.employeeId,
                employeeName = user.fullName,
                employeeType = user.employeeType.name,
                depot = user.depot,
                dutyDate = s.dutyDate.ifBlank { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date()) },
                dutyNumber = s.dutyNumber.ifBlank { "DRAFT" },
                busNumber = s.busNumber,
                busRegistrationNumber = s.busRegistrationNumber,
                route = s.route,
                routeNumber = s.routeNumber,
                startPoint = s.startPoint,
                endPoint = s.endPoint,
                dutyStartTime = s.dutyStartTime,
                dutyEndTime = s.dutyEndTime,
                shift = s.shift,
                totalKm = km,
                income = inc,
                passengerCount = s.passengerCount.toIntOrNull() ?: 0,
                loadFactor = s.loadFactor.toDoubleOrNull() ?: 0.0,
                numberOfTrips = s.numberOfTrips.toIntOrNull() ?: 1,
                ticketCollection = s.ticketCollection.toDoubleOrNull() ?: inc,
                cashCollection = s.cashCollection.toDoubleOrNull() ?: inc,
                remarks = s.remarks,
                status = DutyStatus.DRAFT
            )

            val result = dutyRepository.saveDraft(record)
            result.fold(
                onSuccess = { saved ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        submittedRecordId = saved.recordId,
                        successMessage = "Draft saved successfully."
                    )
                    onSuccess(saved.recordId)
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.localizedMessage ?: "Failed to save draft."
                    )
                }
            )
        }
    }
}
