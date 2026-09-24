package com.example.ui.screens.feedduty

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.User
import java.util.Calendar
import java.util.Locale
import com.example.ui.components.FooterBranding
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent

@Composable
fun FeedDutyScreen(
    viewModel: FeedDutyViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val datePickerDialog = remember(context, state.dutyDate) {
        val cal = Calendar.getInstance()
        if (state.dutyDate.isNotBlank()) {
            try {
                val parts = state.dutyDate.split("-")
                if (parts.size == 3) {
                    cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                }
            } catch (_: Exception) {}
        }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formatted = String.format(Locale.ENGLISH, "%04d-%02d-%02d", year, month + 1, dayOfMonth)
                viewModel.onDutyDateChange(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().imePadding().testTag("feed_duty_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("feed_duty_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "FEED DUTY — ENTRY FORM",
                        color = CyanAccent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "UPSRTC Roadways Operational Log System",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                user?.let { currentUser ->
                    // Protected Employee Identity Card (Auto-Loaded)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF091730), Color(0xFF0C2046))
                                )
                            )
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "AUTHENTICATED EMPLOYEE",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(YellowAccent.copy(alpha = 0.2f))
                                        .border(1.dp, YellowAccent, RoundedCornerShape(50.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "PROTECTED",
                                        color = YellowAccent,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Employee Name", color = TextGray, fontSize = 11.sp)
                                    Text(currentUser.fullName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("ID / CND", color = TextGray, fontSize = 11.sp)
                                    Text(currentUser.employeeId, color = CyanAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Employee Type", color = TextGray, fontSize = 11.sp)
                                    Text(currentUser.employeeType.name, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Depot", color = TextGray, fontSize = 11.sp)
                                    Text(currentUser.depot, color = YellowAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Error Message
                    AnimatedVisibility(visible = state.errorMessage != null) {
                        state.errorMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(RedAccent.copy(alpha = 0.15f))
                                    .border(1.dp, RedAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(text = msg, color = RedAccent, fontSize = 12.sp)
                            }
                        }
                    }

                    // Success Message
                    AnimatedVisibility(visible = state.successMessage != null) {
                        state.successMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 14.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GreenAccent.copy(alpha = 0.15f))
                                    .border(1.dp, GreenAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Column {
                                    Text(text = msg, color = GreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = onNavigateToLogs,
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("View in Month Logs →", color = CyanAccent, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    // Section: DUTY DETAILS
                    SectionHeader(title = "DUTY DETAILS", color = CyanAccent)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(modifier = Modifier.weight(1.3f)) {
                            OutlinedTextField(
                                value = state.dutyDate,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Duty Date (Calendar) *") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.CalendarToday,
                                        contentDescription = "Duty Date",
                                        tint = CyanAccent
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { datePickerDialog.show() }) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Open Calendar",
                                            tint = YellowAccent
                                        )
                                    }
                                },
                                singleLine = true,
                                colors = feedTextFieldColors(),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("duty_date_input")
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { datePickerDialog.show() }
                            )
                        }

                        OutlinedTextField(
                            value = state.dutyNumber,
                            onValueChange = { viewModel.onDutyNumberChange(it) },
                            label = { Text("Duty No. *") },
                            placeholder = { Text("e.g. D-04") },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).testTag("duty_number_input")
                        )
                    }

                    // Quick Calendar Pick Options
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanAccent.copy(alpha = 0.15f))
                                .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                .clickable { datePickerDialog.show() }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("📅 Open Calendar", color = CyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F2144))
                                .border(1.dp, Color(0xFF1E3A70), RoundedCornerShape(8.dp))
                                .clickable {
                                    val now = Calendar.getInstance()
                                    val formatted = String.format(Locale.ENGLISH, "%04d-%02d-%02d", now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH))
                                    viewModel.onDutyDateChange(formatted)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("Today", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF0F2144))
                                .border(1.dp, Color(0xFF1E3A70), RoundedCornerShape(8.dp))
                                .clickable {
                                    val cal = Calendar.getInstance()
                                    cal.add(Calendar.DAY_OF_MONTH, -1)
                                    val formatted = String.format(Locale.ENGLISH, "%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
                                    viewModel.onDutyDateChange(formatted)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text("Yesterday", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.busNumber,
                            onValueChange = { viewModel.onBusNumberChange(it) },
                            label = { Text("Bus Number *") },
                            placeholder = { Text("e.g. UP 15 AT 4210") },
                            leadingIcon = { Icon(Icons.Default.DirectionsBus, null, tint = CyanAccent) },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.2f).testTag("bus_number_input")
                        )

                        OutlinedTextField(
                            value = state.shift,
                            onValueChange = { viewModel.onShiftChange(it) },
                            label = { Text("Shift") },
                            placeholder = { Text("Morning / Evening") },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).testTag("duty_shift_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.route,
                        onValueChange = { viewModel.onRouteChange(it) },
                        label = { Text("Route (From - To) *") },
                        placeholder = { Text("e.g. Meerut - Delhi ISBT") },
                        leadingIcon = { Icon(Icons.Default.Route, null, tint = CyanAccent) },
                        singleLine = true,
                        colors = feedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("route_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.dutyStartTime,
                            onValueChange = { viewModel.onDutyStartTimeChange(it) },
                            label = { Text("Start Time") },
                            placeholder = { Text("06:00") },
                            leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = CyanAccent) },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("start_time_input")
                        )

                        OutlinedTextField(
                            value = state.dutyEndTime,
                            onValueChange = { viewModel.onDutyEndTimeChange(it) },
                            label = { Text("End Time") },
                            placeholder = { Text("14:30") },
                            leadingIcon = { Icon(Icons.Default.AccessTime, null, tint = CyanAccent) },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("end_time_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Section: OPERATIONAL DATA
                    SectionHeader(title = "OPERATIONAL & FINANCIAL DATA", color = YellowAccent)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.totalKm,
                            onValueChange = { viewModel.onTotalKmChange(it) },
                            label = { Text("Total KM *") },
                            placeholder = { Text("e.g. 180") },
                            leadingIcon = { Icon(Icons.Default.Speed, null, tint = YellowAccent) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("total_km_input")
                        )

                        OutlinedTextField(
                            value = state.income,
                            onValueChange = { viewModel.onIncomeChange(it) },
                            label = { Text("Income (₹) *") },
                            placeholder = { Text("e.g. 14250") },
                            leadingIcon = { Icon(Icons.Default.CurrencyRupee, null, tint = YellowAccent) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("income_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.passengerCount,
                            onValueChange = { viewModel.onPassengerCountChange(it) },
                            label = { Text("Passenger Count") },
                            placeholder = { Text("e.g. 85") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("passenger_count_input")
                        )

                        OutlinedTextField(
                            value = state.loadFactor,
                            onValueChange = { viewModel.onLoadFactorChange(it) },
                            label = { Text("Load Factor %") },
                            placeholder = { Text("e.g. 88.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).testTag("load_factor_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.numberOfTrips,
                            onValueChange = { viewModel.onNumberOfTripsChange(it) },
                            label = { Text("Trips Count") },
                            placeholder = { Text("e.g. 2") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.8f).testTag("trips_input")
                        )

                        OutlinedTextField(
                            value = state.remarks,
                            onValueChange = { viewModel.onRemarksChange(it) },
                            label = { Text("Remarks") },
                            placeholder = { Text("Traffic / weather / special notes") },
                            singleLine = true,
                            colors = feedTextFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.4f).testTag("remarks_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // ACTION BUTTONS
                    // 1. SUBMIT DUTY (Cyan Glowing Primary)
                    Button(
                        onClick = { viewModel.promptConfirmSubmit() },
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyanAccent,
                            contentColor = DarkNavyBg
                        ),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("submit_duty_button")
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = DarkNavyBg, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = "SUBMIT DUTY RECORD",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. SAVE DRAFT & CANCEL
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.saveDraft(currentUser) { } },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = YellowAccent),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(YellowAccent, YellowAccent.copy(alpha = 0.5f)))),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("save_draft_button")
                        ) {
                            Text("SAVE DRAFT", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextGray),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(BorderDark, BorderDark))),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("cancel_feed_duty_button")
                        ) {
                            Text("CANCEL", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            FooterBranding()
        }
    }

    // Confirmation Dialog before submission
    if (state.showConfirmSubmitDialog) {
        user?.let { currentUser ->
            AlertDialog(
                onDismissRequest = { viewModel.dismissConfirmDialog() },
                title = {
                    Text(
                        text = "Verify Duty Details",
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Please verify all duty details before submitting.",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("• Date: ${state.dutyDate}", color = TextGray, fontSize = 13.sp)
                        Text("• Duty #: ${state.dutyNumber}", color = TextGray, fontSize = 13.sp)
                        Text("• Bus: ${state.busNumber}", color = TextGray, fontSize = 13.sp)
                        Text("• Route: ${state.route}", color = TextGray, fontSize = 13.sp)
                        Text("• Distance: ${state.totalKm} KM", color = TextGray, fontSize = 13.sp)
                        Text("• Total Income: ₹${state.income}", color = YellowAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.submitDuty(currentUser) { } },
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg),
                        modifier = Modifier.testTag("confirm_submit_duty_button")
                    ) {
                        Text("Confirm & Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissConfirmDialog() }) {
                        Text("Review", color = TextWhite)
                    }
                },
                containerColor = DarkNavyCard
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun feedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = TextGray,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanAccent
)
