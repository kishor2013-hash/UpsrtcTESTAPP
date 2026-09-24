package com.example.ui.screens.showdata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DutyRecord
import com.example.data.model.DutyStatus
import com.example.ui.components.FooterBranding
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent

@Composable
fun ShowDataScreen(
    viewModel: ShowDataViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val records by viewModel.filteredRecords.collectAsState()
    val calcs by viewModel.calculations.collectAsState()

    val monthNames = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().testTag("show_data_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("show_data_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = YellowAccent
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "DUTY LOGS & REPORTS",
                                color = YellowAccent,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "Verified Monthly Records & Analytics",
                                color = TextGray,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // PDF Export Button
                    user?.let { currentUser ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFF221708))
                                .border(1.2.dp, YellowAccent, RoundedCornerShape(50.dp))
                                .clickable { viewModel.exportPdf(context, currentUser) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("export_pdf_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "Export PDF",
                                    tint = YellowAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PDF",
                                    color = YellowAccent,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Month Selector Scroll Row
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        monthNames.forEachIndexed { index, mName ->
                            val monthNum = index + 1
                            val isSelected = state.selectedMonth == monthNum
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) YellowAccent else DarkNavySurface)
                                    .border(1.dp, if (isSelected) YellowAccent else BorderDark, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.onMonthChange(monthNum) }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$mName ${state.selectedYear}",
                                    color = if (isSelected) DarkNavyBg else TextWhite,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar & Status Filter
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search by Bus #, Route, Duty #, Record ID") },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = YellowAccent) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YellowAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().testTag("show_data_search_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ALL", "SUBMITTED", "DRAFT").forEach { st ->
                            val active = state.statusFilter == st
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50.dp))
                                    .background(if (active) CyanAccent.copy(alpha = 0.2f) else DarkNavySurface)
                                    .border(1.dp, if (active) CyanAccent else BorderDark, RoundedCornerShape(50.dp))
                                    .clickable { viewModel.onStatusFilterChange(st) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = st,
                                    color = if (active) CyanAccent else TextGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Summary Calculations Deck
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "MONTHLY PERFORMANCE SUMMARY",
                        color = YellowAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CalcCard(
                            label = "Total Duties",
                            value = "${calcs.totalDuties}",
                            accent = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        CalcCard(
                            label = "Total KM",
                            value = "%.0f KM".format(calcs.totalKm),
                            accent = GreenAccent,
                            modifier = Modifier.weight(1f)
                        )
                        CalcCard(
                            label = "Total Income",
                            value = "₹%.0f".format(calcs.totalIncome),
                            accent = YellowAccent,
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CalcCard(
                            label = "Avg KM/Duty",
                            value = "%.1f KM".format(calcs.avgKm),
                            accent = TextWhite,
                            modifier = Modifier.weight(1f)
                        )
                        CalcCard(
                            label = "Avg Income",
                            value = "₹%.0f".format(calcs.avgIncome),
                            accent = TextWhite,
                            modifier = Modifier.weight(1f)
                        )
                        CalcCard(
                            label = "Avg Load Factor",
                            value = "%.1f%%".format(calcs.avgLoadFactor),
                            accent = PurpleAccent,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Duty Records Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DUTY ENTRIES (${records.size})",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Empty State
            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkNavySurface)
                            .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Assessment,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No duty records found.",
                                color = TextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Records will appear here once submitted.",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Duty Record Cards
            items(records, key = { it.recordId }) { record ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    DutyRecordCard(
                        record = record,
                        onViewDetail = { viewModel.selectRecordForDetail(record) },
                        onDelete = { viewModel.promptDeleteRecord(record) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                FooterBranding()
            }
        }
    }

    // Detail Modal Dialog
    state.selectedRecordForDetail?.let { detail ->
        AlertDialog(
            onDismissRequest = { viewModel.selectRecordForDetail(null) },
            title = {
                Text(
                    text = "Duty ${detail.dutyNumber} Details",
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    DetailRow("Record ID", detail.recordId)
                    DetailRow("Employee", "${detail.employeeName} (${detail.employeeId})")
                    DetailRow("Depot", detail.depot)
                    DetailRow("Date", detail.dutyDate)
                    DetailRow("Bus Number", detail.busNumber)
                    DetailRow("Route", detail.route)
                    DetailRow("Shift", detail.shift)
                    DetailRow("Time", "${detail.dutyStartTime} - ${detail.dutyEndTime}")
                    DetailRow("Distance", "${detail.totalKm} KM")
                    DetailRow("Income", "₹${detail.income}")
                    DetailRow("Passengers", "${detail.passengerCount}")
                    DetailRow("Load Factor", "${detail.loadFactor}%")
                    DetailRow("Trips", "${detail.numberOfTrips}")
                    DetailRow("Status", detail.status.name)
                    if (detail.remarks.isNotBlank()) {
                        DetailRow("Remarks", detail.remarks)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.selectRecordForDetail(null) },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg)
                ) {
                    Text("Close")
                }
            },
            containerColor = DarkNavyCard
        )
    }

    // Delete Confirmation Dialog
    state.recordToDelete?.let { toDelete ->
        AlertDialog(
            onDismissRequest = { viewModel.promptDeleteRecord(null) },
            title = {
                Text("Confirm Delete", color = RedAccent, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete duty record ${toDelete.dutyNumber} (${toDelete.recordId})? This action cannot be undone.",
                    color = TextWhite
                )
            },
            confirmButton = {
                user?.let { currentUser ->
                    Button(
                        onClick = { viewModel.deleteRecord(currentUser) },
                        colors = ButtonDefaults.buttonColors(containerColor = RedAccent, contentColor = TextWhite)
                    ) {
                        Text("Delete Record")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.promptDeleteRecord(null) }) {
                    Text("Cancel", color = TextWhite)
                }
            },
            containerColor = DarkNavyCard
        )
    }
}

@Composable
private fun CalcCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurface)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(text = label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun DutyRecordCard(
    record: DutyRecord,
    onViewDetail: () -> Unit,
    onDelete: () -> Unit
) {
    val isSubmitted = record.status == DutyStatus.SUBMITTED
    val statusColor = if (isSubmitted) GreenAccent else YellowAccent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0C1936))
            .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
            .clickable { onViewDetail() }
            .padding(14.dp)
            .testTag("duty_record_item_${record.recordId}")
    ) {
        Column {
            // Row 1: Duty # & Bus # + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyanAccent.copy(alpha = 0.2f))
                            .border(1.dp, CyanAccent, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = record.dutyNumber,
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = record.busNumber,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor, RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = record.status.name,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Row 2: Route & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.route,
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = record.dutyDate,
                    color = TextGray,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Financial & Metrics (KM, Income, Load Factor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF09142A))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Distance: ${record.totalKm} KM", color = TextWhite, fontSize = 11.sp)
                Text("Income: ₹${record.income}", color = YellowAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("LF: ${record.loadFactor}%", color = CyanAccent, fontSize = 11.sp)

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RedAccent.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextGray, fontSize = 12.sp)
        Text(text = value, color = TextWhite, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
