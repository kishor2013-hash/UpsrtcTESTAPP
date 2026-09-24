package com.example.ui.screens.admin

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PortalSettings
import com.example.data.model.User
import com.example.data.model.UserRole
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
fun AdminScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val users by viewModel.allUsers.collectAsState()
    val duties by viewModel.allDuties.collectAsState()
    val settings by viewModel.portalSettings.collectAsState()
    val logs by viewModel.auditLogs.collectAsState()

    val isAdmin = user?.let { it.role == UserRole.ADMIN || it.role == UserRole.SUPER_ADMIN } ?: false

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().testTag("admin_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("admin_back_button")
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
                        text = "ADMINISTRATIVE CONTROL CONSOLE",
                        color = CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "System Administration, Access Control & Logs",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            if (!isAdmin) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkNavySurface)
                        .border(1.dp, RedAccent, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "ACCESS DENIED",
                            color = RedAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "You do not have administrative privileges to view this console.",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }
                }
                return@Scaffold
            }

            // Tabs Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Overview" to 0,
                    "Employees" to 1,
                    "Portal URLs" to 2,
                    "Audit Trail" to 3,
                    "Cloud Sync" to 4
                ).forEach { (label, index) ->
                    val isSelected = state.selectedTab == index
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) CyanAccent else DarkNavySurface)
                            .border(1.dp, if (isSelected) CyanAccent else BorderDark, RoundedCornerShape(10.dp))
                            .clickable { viewModel.selectTab(index) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) DarkNavyBg else TextWhite,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Message Banner
            AnimatedVisibility(visible = state.message != null) {
                state.message?.let { msg ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GreenAccent.copy(alpha = 0.15f))
                            .border(1.dp, GreenAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(text = msg, color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Main Tab Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                when (state.selectedTab) {
                    0 -> {
                        // Overview Tab
                        item {
                            val totalKm = duties.sumOf { it.totalKm }
                            val totalRev = duties.sumOf { it.income }
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                AdminStatCard("Total Registered Employees", "${users.size}", CyanAccent)
                                AdminStatCard("Total Logged Duties", "${duties.size}", YellowAccent)
                                AdminStatCard("Total Fleet Distance", "%.1f KM".format(totalKm), GreenAccent)
                                AdminStatCard("Total Operational Revenue", "₹%.2f".format(totalRev), CyanAccent)
                            }
                        }
                    }

                    1 -> {
                        // Employees Tab
                        items(users, key = { it.uid }) { targetUser ->
                            user?.let { currentUser ->
                                EmployeeManagementCard(
                                    targetUser = targetUser,
                                    currentUser = currentUser,
                                    onRoleToggle = { newRole ->
                                        viewModel.updateUserRole(targetUser, newRole, currentUser)
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }

                    2 -> {
                        // Portal URLs Configuration
                        item {
                            user?.let { currentUser ->
                                PortalUrlsEditor(
                                    initialSettings = settings,
                                    onSave = { updated ->
                                        viewModel.updateSettings(updated, currentUser)
                                    }
                                )
                            }
                        }
                    }

                    3 -> {
                        // Audit Logs Tab
                        items(logs, key = { it.id }) { log ->
                            val timeStr = java.text.SimpleDateFormat("dd MMM HH:mm", java.util.Locale.ENGLISH).format(java.util.Date(log.timestamp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkNavySurface)
                                    .border(1.dp, BorderDark, RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = log.action, color = YellowAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(text = timeStr, color = TextGray, fontSize = 10.sp)
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(text = "User: ${log.userName}", color = TextWhite, fontSize = 11.sp)
                                    Text(text = log.details, color = TextGray, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    4 -> {
                        // Cloud Sync Tab
                        item {
                            user?.let { currentUser ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(DarkNavySurface)
                                        .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
                                        .padding(18.dp)
                                ) {
                                    Column {
                                        Text("Firebase Firestore Cloud Sync", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Sync all local Room database duty records to Google Firebase Firestore collections in the cloud for multi-device backup and disaster recovery.",
                                            color = TextGray,
                                            fontSize = 12.sp
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))

                                        Button(
                                            onClick = { viewModel.triggerCloudSync(currentUser) },
                                            enabled = !state.isSyncing,
                                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg),
                                            shape = RoundedCornerShape(50.dp),
                                            modifier = Modifier.fillMaxWidth().height(48.dp)
                                        ) {
                                            if (state.isSyncing) {
                                                CircularProgressIndicator(color = DarkNavyBg, modifier = Modifier.size(22.dp))
                                            } else {
                                                Text("SYNC ALL DUTIES TO FIRESTORE", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    FooterBranding()
                }
            }
        }
    }
}

@Composable
private fun AdminStatCard(title: String, value: String, accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkNavySurface)
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = accent, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun EmployeeManagementCard(
    targetUser: User,
    currentUser: User,
    onRoleToggle: (UserRole) -> Unit
) {
    val isUserAdmin = targetUser.role == UserRole.ADMIN || targetUser.role == UserRole.SUPER_ADMIN

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkNavySurface)
            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(targetUser.fullName, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("ID: ${targetUser.employeeId} • ${targetUser.employeeType.name}", color = CyanAccent, fontSize = 12.sp)
                Text("Depot: ${targetUser.depot} • Role: ${targetUser.role.name}", color = TextGray, fontSize = 11.sp)
            }

            if (currentUser.uid != targetUser.uid) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (isUserAdmin) RedAccent.copy(alpha = 0.2f) else CyanAccent.copy(alpha = 0.2f))
                        .border(1.dp, if (isUserAdmin) RedAccent else CyanAccent, RoundedCornerShape(50.dp))
                        .clickable {
                            val newRole = if (isUserAdmin) UserRole.USER else UserRole.ADMIN
                            onRoleToggle(newRole)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isUserAdmin) "Revoke Admin" else "Make Admin",
                        color = if (isUserAdmin) RedAccent else CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PortalUrlsEditor(
    initialSettings: PortalSettings,
    onSave: (PortalSettings) -> Unit
) {
    var paySlip by remember { mutableStateOf(initialSettings.paySlipUrl) }
    var pfPortal by remember { mutableStateOf(initialSettings.pfPortalUrl) }
    var challan by remember { mutableStateOf(initialSettings.challanUrl) }
    var manavSampada by remember { mutableStateOf(initialSettings.manavSampadaUrl) }
    var phone by remember { mutableStateOf(initialSettings.footerPhone) }
    var website by remember { mutableStateOf(initialSettings.footerWebsite) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("EXTERNAL PORTAL INTEGRATIONS", color = CyanAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = paySlip,
            onValueChange = { paySlip = it },
            label = { Text("Pay Slip Portal URL") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = pfPortal,
            onValueChange = { pfPortal = it },
            label = { Text("PF / EPFO Portal URL") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = challan,
            onValueChange = { challan = it },
            label = { Text("Challan Portal URL") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = manavSampada,
            onValueChange = { manavSampada = it },
            label = { Text("Manav Sampada URL") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Text("GROFASTO BRANDING & CONTACT", color = YellowAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Contact Phone") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = website,
            onValueChange = { website = it },
            label = { Text("Website") },
            colors = adminFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val updated = initialSettings.copy(
                    paySlipUrl = paySlip,
                    pfPortalUrl = pfPortal,
                    challanUrl = challan,
                    manavSampadaUrl = manavSampada,
                    footerPhone = phone,
                    footerWebsite = website
                )
                onSave(updated)
            },
            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg),
            shape = RoundedCornerShape(50.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("SAVE CONFIGURATION", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun adminFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = TextGray,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanAccent
)
