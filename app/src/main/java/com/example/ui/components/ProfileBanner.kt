package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.User
import com.example.data.model.UserRole
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent

@Composable
fun ProfileBanner(
    user: User,
    onProfileClick: () -> Unit,
    onAdminClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLogoutConfirm by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF091730),
                        Color(0xFF0A1C3B)
                    )
                )
            )
            .border(
                1.2.dp,
                Color(0xFF1E3A70),
                RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("profile_banner")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Avatar + Details
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Avatar Frame with Gold Border
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13233E))
                        .border(2.dp, YellowAccent, CircleShape)
                        .clickable { onProfileClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (user.photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = user.photoUrl,
                            contentDescription = "Employee Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Employee Avatar",
                            tint = YellowAccent,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    // Line 1: Name and CND in Cyan
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = user.fullName,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "  |  ${user.employeeId}  |",
                            color = CyanAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Line 2: Depot in bold Yellow
                    Text(
                        text = user.depot.ifBlank { "HEADQUARTERS" }.uppercase(),
                        color = YellowAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Line 3: Verified designation • Depot
                    Text(
                        text = "UPSRTC Verified ${user.employeeType.name.lowercase().replaceFirstChar { it.uppercase() }} • ${user.depot.ifBlank { "DEPOT" }}",
                        color = Color(0xFF7C92B1),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Right: Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Admin button if permitted
                if (user.role == UserRole.ADMIN || user.role == UserRole.SUPER_ADMIN) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F254B))
                            .border(1.dp, CyanAccent.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .clickable { onAdminClick() }
                            .testTag("admin_panel_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Panel",
                            tint = CyanAccent,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                // Profile button (User icon inside dark rounded box with blue border)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F254B))
                        .border(1.2.dp, Color(0xFF2563EB), RoundedCornerShape(10.dp))
                        .clickable { onProfileClick() }
                        .testTag("user_profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = "Edit Profile",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Logout button (Exit icon inside dark rounded box with red border)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF28111D))
                        .border(1.2.dp, RedAccent.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                        .clickable { showLogoutConfirm = true }
                        .testTag("logout_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = RedAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }

    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            title = { Text(text = "Confirm Logout", color = TextWhite) },
            text = { Text(text = "Are you sure you want to log out of UPSRTC Duty Portal?", color = TextGray) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        onLogoutClick()
                    }
                ) {
                    Text("Logout", color = RedAccent)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Cancel", color = TextWhite)
                }
            },
            containerColor = DarkNavySurface
        )
    }
}
