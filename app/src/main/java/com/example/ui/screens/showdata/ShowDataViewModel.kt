package com.example.ui.screens.showdata

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.DutyRecord
import com.example.data.model.DutyStatus
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.data.repository.AuthRepository
import com.example.data.repository.DutyRepository
import com.example.ui.util.PdfGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class MonthlyCalculations(
    val totalDuties: Int = 0,
    val totalKm: Double = 0.0,
    val totalIncome: Double = 0.0,
    val avgKm: Double = 0.0,
    val avgIncome: Double = 0.0,
    val avgLoadFactor: Double = 0.0,
    val totalTrips: Int = 0
)

data class ShowDataUiState(
    val selectedYear: Int = 2026,
    val selectedMonth: Int = 9, // 1 to 12
    val searchQuery: String = "",
    val statusFilter: String = "ALL", // ALL, SUBMITTED, DRAFT
    val selectedRecordForDetail: DutyRecord? = null,
    val recordToDelete: DutyRecord? = null,
    val message: String? = null,
    val isGeneratingPdf: Boolean = false
)

class ShowDataViewModel(
    private val authRepository: AuthRepository,
    private val dutyRepository: DutyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ShowDataUiState(
            selectedYear = Calendar.getInstance().get(Calendar.YEAR),
            selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        )
    )
    val uiState: StateFlow<ShowDataUiState> = _uiState.asStateFlow()

    val currentUser = authRepository.currentUser

    // Raw records stream from database
    private val allDutyRecords = dutyRepository.getAllRecords()

    // Filtered records and calculations
    val filteredRecords: StateFlow<List<DutyRecord>> = combine(
        currentUser,
        allDutyRecords,
        _uiState
    ) { user, records, state ->
        if (user == null) return@combine emptyList()

        val isStaffAdmin = user.role == UserRole.ADMIN || user.role == UserRole.SUPER_ADMIN
        val userRecords = if (isStaffAdmin) records else records.filter { it.userId == user.uid }

        val monthPrefix = "%04d-%02d".format(state.selectedYear, state.selectedMonth)

        userRecords.filter { record ->
            val matchesMonth = record.dutyDate.startsWith(monthPrefix)
            val matchesSearch = state.searchQuery.isBlank() ||
                record.busNumber.contains(state.searchQuery, ignoreCase = true) ||
                record.route.contains(state.searchQuery, ignoreCase = true) ||
                record.dutyNumber.contains(state.searchQuery, ignoreCase = true) ||
                record.recordId.contains(state.searchQuery, ignoreCase = true)
            val matchesStatus = when (state.statusFilter) {
                "SUBMITTED" -> record.status == DutyStatus.SUBMITTED
                "DRAFT" -> record.status == DutyStatus.DRAFT
                else -> true
            }

            matchesMonth && matchesSearch && matchesStatus
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val calculations: StateFlow<MonthlyCalculations> = filteredRecords.combine(_uiState) { list, _ ->
        if (list.isEmpty()) {
            MonthlyCalculations()
        } else {
            val totalDuties = list.size
            val totalKm = list.sumOf { it.totalKm }
            val totalIncome = list.sumOf { it.income }
            val totalTrips = list.sumOf { it.numberOfTrips }
            val avgKm = if (totalDuties > 0) totalKm / totalDuties else 0.0
            val avgIncome = if (totalDuties > 0) totalIncome / totalDuties else 0.0
            val avgLf = if (totalDuties > 0) list.map { it.loadFactor }.average() else 0.0

            MonthlyCalculations(
                totalDuties = totalDuties,
                totalKm = totalKm,
                totalIncome = totalIncome,
                avgKm = avgKm,
                avgIncome = avgIncome,
                avgLoadFactor = avgLf,
                totalTrips = totalTrips
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MonthlyCalculations()
    )

    fun onMonthChange(month: Int) {
        _uiState.value = _uiState.value.copy(selectedMonth = month)
    }

    fun onYearChange(year: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year)
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun onStatusFilterChange(status: String) {
        _uiState.value = _uiState.value.copy(statusFilter = status)
    }

    fun selectRecordForDetail(record: DutyRecord?) {
        _uiState.value = _uiState.value.copy(selectedRecordForDetail = record)
    }

    fun promptDeleteRecord(record: DutyRecord?) {
        _uiState.value = _uiState.value.copy(recordToDelete = record)
    }

    fun deleteRecord(user: User) {
        val record = _uiState.value.recordToDelete ?: return
        viewModelScope.launch {
            val isAdmin = user.role == UserRole.ADMIN || user.role == UserRole.SUPER_ADMIN
            val res = dutyRepository.deleteDuty(record.recordId, user.uid, user.fullName, isAdmin)
            res.fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        recordToDelete = null,
                        selectedRecordForDetail = null,
                        message = "Duty record deleted."
                    )
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        recordToDelete = null,
                        message = err.localizedMessage ?: "Failed to delete record."
                    )
                }
            )
        }
    }

    fun exportPdf(context: Context, user: User) {
        val records = filteredRecords.value
        val calc = calculations.value
        val monthNames = arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        val monthStr = "${monthNames[_uiState.value.selectedMonth - 1]} ${_uiState.value.selectedYear}"

        PdfGenerator.generateAndShareDutyReport(
            context = context,
            user = user,
            monthName = monthStr,
            records = records,
            totalDuties = calc.totalDuties,
            totalKm = calc.totalKm,
            totalIncome = calc.totalIncome,
            avgKm = calc.avgKm,
            avgIncome = calc.avgIncome,
            avgLoadFactor = calc.avgLoadFactor
        )
    }
}
