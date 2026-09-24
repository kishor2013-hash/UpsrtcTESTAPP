package com.example.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FooterBranding
import com.example.ui.components.PortalCard
import com.example.ui.components.ProfileBanner
import com.example.ui.components.UpsrtcHeader
import com.example.ui.theme.BlueAccent
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent
import com.example.ui.util.PortalLauncher

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToFeedDuty: () -> Unit,
    onNavigateToShowData: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user by viewModel.currentUser.collectAsState()
    val settings by viewModel.portalSettings.collectAsState()

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().testTag("main_dashboard_screen")
    ) { innerPadding ->
        user?.let { currentUser ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header (Spans full 2 columns)
                item(span = { GridItemSpan(2) }) {
                    UpsrtcHeader(
                        appName = settings.appName,
                        appVersion = settings.appVersion
                    )
                }

                // Profile Banner (Spans full 2 columns)
                item(span = { GridItemSpan(2) }) {
                    ProfileBanner(
                        user = currentUser,
                        onProfileClick = onNavigateToProfile,
                        onAdminClick = onNavigateToAdmin,
                        onLogoutClick = {
                            viewModel.logout { onLogout() }
                        }
                    )
                }

                // Section Title: OPERATIONAL CONTROL DECK
                item(span = { GridItemSpan(2) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(18.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(CyanAccent)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OPERATIONAL CONTROL DECK",
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                // Row 1 - Left: FEED DUTY (Cyan)
                item {
                    PortalCard(
                        title = "FEED DUTY",
                        badgeText = "FEED FORM",
                        description = "Bus no., route, income ₹, load factor & KM with auto-fill.",
                        buttonText = "Open Entry Form →",
                        icon = Icons.Default.DirectionsBus,
                        themeColor = CyanAccent,
                        onClick = onNavigateToFeedDuty,
                        testTag = "card_feed_duty"
                    )
                }

                // Row 1 - Right: SHOW DATA (Yellow)
                item {
                    PortalCard(
                        title = "SHOW DATA",
                        badgeText = "REPORTS",
                        description = "Monthly records, calculations and verified PDF printout.",
                        buttonText = "View Month Logs →",
                        icon = Icons.Default.Assessment,
                        themeColor = YellowAccent,
                        onClick = onNavigateToShowData,
                        testTag = "card_show_data"
                    )
                }

                // Row 2 - Left: PAY SLIP (Blue)
                item {
                    PortalCard(
                        title = "PAY SLIP",
                        badgeText = "PAYROLL",
                        description = "Direct login to UPSRTC Employee Payroll Portal.",
                        buttonText = "Launch Portal →",
                        icon = Icons.Default.Wallet,
                        themeColor = BlueAccent,
                        onClick = {
                            PortalLauncher.launchUrl(context, settings.paySlipUrl)
                        },
                        testTag = "card_pay_slip"
                    )
                }

                // Row 2 - Right: PF PORTAL (Green)
                item {
                    PortalCard(
                        title = "PF PORTAL",
                        badgeText = "EPFO",
                        description = "Unified Member passbook & provident fund claim gateway.",
                        buttonText = "EPFO Passbook →",
                        icon = Icons.Default.Balance,
                        themeColor = GreenAccent,
                        onClick = {
                            PortalLauncher.launchUrl(context, settings.pfPortalUrl)
                        },
                        testTag = "card_pf_portal"
                    )
                }

                // Row 3 - Left: CHALLAN (Red)
                item {
                    PortalCard(
                        title = "CHALLAN",
                        badgeText = "CHALLAN",
                        description = "View/verify traffic or departmental challans.",
                        buttonText = "Open Challan Portal →",
                        icon = Icons.Default.ReceiptLong,
                        themeColor = RedAccent,
                        onClick = {
                            PortalLauncher.launchUrl(context, settings.challanUrl)
                        },
                        testTag = "card_challan"
                    )
                }

                // Row 3 - Right: MANAV SAMPADA (Purple)
                item {
                    PortalCard(
                        title = "MANAV SAMPADA",
                        badgeText = "HR PORTAL",
                        description = "Employee HR/service portal for attendance, leave, service details and login.",
                        buttonText = "Open Manav Sampada →",
                        icon = Icons.Default.Groups,
                        themeColor = PurpleAccent,
                        onClick = {
                            PortalLauncher.launchUrl(context, settings.manavSampadaUrl)
                        },
                        testTag = "card_manav_sampada"
                    )
                }

                // Footer (Spans full 2 columns)
                item(span = { GridItemSpan(2) }) {
                    FooterBranding(
                        modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                    )
                }
            }
        }
    }
}
