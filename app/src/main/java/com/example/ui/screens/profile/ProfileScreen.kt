package com.example.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Work
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.components.FooterBranding
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val user by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onPhotoSelected(it.toString())
        }
    }

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().imePadding().testTag("profile_screen")
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
                    modifier = Modifier.testTag("profile_back_button")
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
                        text = "EMPLOYEE PROFILE & SETTINGS",
                        color = CyanAccent,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Official UPSRTC Records & Account Credentials",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            user?.let { currentUser ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    // Profile Header Card with Avatar & Photo Upload
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkNavySurface)
                            .border(1.2.dp, Color(0xFF1E3A8A), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar Box with Camera Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clickable {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                            ) {
                                if (state.photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = state.photoUrl,
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .border(2.dp, YellowAccent, CircleShape)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF112240))
                                            .border(2.dp, YellowAccent, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = YellowAccent,
                                            modifier = Modifier.size(42.dp)
                                        )
                                    }
                                }

                                // Camera action badge on corner
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(CyanAccent)
                                        .border(2.dp, DarkNavyBg, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Upload Photo",
                                        tint = DarkNavyBg,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (state.fullName.isNotBlank()) state.fullName else currentUser.fullName,
                                    color = TextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "CND: ${currentUser.employeeId} • ${currentUser.employeeType.name}",
                                    color = CyanAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = currentUser.email,
                                    color = TextGray,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Tap to Change Photo Button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(CyanAccent.copy(alpha = 0.15f))
                                        .border(1.dp, CyanAccent.copy(alpha = 0.6f), RoundedCornerShape(50.dp))
                                        .clickable {
                                            photoPickerLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "📷 Change Photo",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Messages
                    AnimatedVisibility(visible = state.errorMessage != null) {
                        state.errorMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RedAccent.copy(alpha = 0.15f))
                                    .padding(10.dp)
                            ) {
                                Text(text = msg, color = RedAccent, fontSize = 12.sp)
                            }
                        }
                    }

                    AnimatedVisibility(visible = state.successMessage != null) {
                        state.successMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreenAccent.copy(alpha = 0.15f))
                                    .padding(10.dp)
                            ) {
                                Text(text = msg, color = GreenAccent, fontSize = 12.sp)
                            }
                        }
                    }

                    // Edit Details Section
                    Text(
                        text = "PERSONAL & DEPOT INFORMATION",
                        color = YellowAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Full Name
                    OutlinedTextField(
                        value = state.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = CyanAccent) },
                        colors = profileFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mobile & DOB
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.mobile,
                            onValueChange = { viewModel.onMobileChange(it) },
                            label = { Text("Mobile Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = CyanAccent) },
                            colors = profileFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.1f).testTag("profile_mobile_input")
                        )

                        OutlinedTextField(
                            value = state.dob,
                            onValueChange = { viewModel.onDobChange(it) },
                            label = { Text("DOB (YYYY-MM-DD)") },
                            leadingIcon = { Icon(Icons.Default.Cake, null, tint = CyanAccent) },
                            colors = profileFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.9f).testTag("profile_dob_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Depot & Code
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = state.depot,
                            onValueChange = { viewModel.onDepotChange(it) },
                            label = { Text("Depot Name") },
                            leadingIcon = { Icon(Icons.Default.Business, null, tint = CyanAccent) },
                            colors = profileFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.3f).testTag("profile_depot_input")
                        )

                        OutlinedTextField(
                            value = state.depotCode,
                            onValueChange = { viewModel.onDepotCodeChange(it) },
                            label = { Text("Depot Code") },
                            colors = profileFieldColors(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(0.7f).testTag("profile_depot_code_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Designation
                    OutlinedTextField(
                        value = state.designation,
                        onValueChange = { viewModel.onDesignationChange(it) },
                        label = { Text("Designation") },
                        leadingIcon = { Icon(Icons.Default.Work, null, tint = CyanAccent) },
                        colors = profileFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("profile_designation_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save Profile Button
                    Button(
                        onClick = { viewModel.updateProfile(currentUser) },
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("profile_save_button")
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = DarkNavyBg, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text("SAVE PROFILE & PHOTO CHANGES", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Change Password Section
                    Text(
                        text = "CHANGE PASSWORD",
                        color = RedAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = state.newPass,
                        onValueChange = { viewModel.onNewPassChange(it) },
                        label = { Text("New Password (min 6 chars)") },
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = RedAccent) },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = profileFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("profile_new_password_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.changePassword(currentUser) },
                        enabled = !state.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = RedAccent, contentColor = TextWhite),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("profile_change_pass_button")
                    ) {
                        Text("UPDATE PASSWORD", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            FooterBranding()
        }
    }
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = TextGray,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanAccent
)
