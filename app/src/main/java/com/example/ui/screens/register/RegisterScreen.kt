package com.example.ui.screens.register

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmployeeType
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
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBackToLogin,
                    modifier = Modifier.testTag("register_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = CyanAccent
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "NEW EMPLOYEE REGISTRATION",
                        color = CyanAccent,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "UPSRTC Roadways Official Portal",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                // Error Alert
                AnimatedVisibility(visible = state.errorMessage != null) {
                    state.errorMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(RedAccent.copy(alpha = 0.15f))
                                .border(1.dp, RedAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("register_error_banner")
                        ) {
                            Text(
                                text = msg,
                                color = RedAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Section 1: PERSONAL INFORMATION
                SectionTitle(title = "PERSONAL INFORMATION", accentColor = CyanAccent)

                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = { Text("Full Name *") },
                    placeholder = { Text("Enter your legal name") },
                    leadingIcon = { Icon(Icons.Default.Person, null, tint = CyanAccent) },
                    singleLine = true,
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_fullname_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.mobile,
                        onValueChange = { viewModel.onMobileChange(it) },
                        label = { Text("Mobile (10 digits) *") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = { Icon(Icons.Default.Phone, null, tint = CyanAccent) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = customTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("reg_mobile_input")
                    )

                    OutlinedTextField(
                        value = state.dob,
                        onValueChange = { viewModel.onDobChange(it) },
                        label = { Text("Date of Birth") },
                        placeholder = { Text("DD-MM-YYYY") },
                        singleLine = true,
                        colors = customTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("reg_dob_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Email Address *") },
                    placeholder = { Text("name@example.com") },
                    leadingIcon = { Icon(Icons.Default.Email, null, tint = CyanAccent) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: EMPLOYEE INFORMATION
                SectionTitle(title = "EMPLOYEE INFORMATION", accentColor = YellowAccent)

                Text(
                    text = "Select Employee Type *",
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Segmented Selector: DRIVER vs CONDUCTOR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkNavySurface)
                        .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val isDriver = state.employeeType == EmployeeType.DRIVER
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDriver) CyanAccent else Color.Transparent)
                            .clickable { viewModel.onEmployeeTypeChange(EmployeeType.DRIVER) }
                            .padding(vertical = 12.dp)
                            .testTag("select_driver_type"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "DRIVER",
                            color = if (isDriver) DarkNavyBg else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    val isConductor = state.employeeType == EmployeeType.CONDUCTOR
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isConductor) YellowAccent else Color.Transparent)
                            .clickable { viewModel.onEmployeeTypeChange(EmployeeType.CONDUCTOR) }
                            .padding(vertical = 12.dp)
                            .testTag("select_conductor_type"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "CONDUCTOR",
                            color = if (isConductor) DarkNavyBg else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.employeeId,
                    onValueChange = { viewModel.onEmployeeIdChange(it) },
                    label = { Text("Employee ID / CND Number *") },
                    placeholder = { Text(if (state.employeeType == EmployeeType.DRIVER) "e.g. DRV 2104" else "e.g. CND 1901") },
                    leadingIcon = { Icon(Icons.Default.DirectionsBus, null, tint = YellowAccent) },
                    singleLine = true,
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_employee_id_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: DEPOT & DESIGNATION
                SectionTitle(title = "DEPOT & DESIGNATION", accentColor = GreenAccent)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = state.depot,
                        onValueChange = { viewModel.onDepotChange(it) },
                        label = { Text("Depot Name *") },
                        placeholder = { Text("e.g. SOHRAB GATE DEPOT") },
                        leadingIcon = { Icon(Icons.Default.Business, null, tint = GreenAccent) },
                        singleLine = true,
                        colors = customTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1.4f).testTag("reg_depot_input")
                    )

                    OutlinedTextField(
                        value = state.depotCode,
                        onValueChange = { viewModel.onDepotCodeChange(it) },
                        label = { Text("Depot Code") },
                        placeholder = { Text("e.g. SGB") },
                        singleLine = true,
                        colors = customTextFieldColors(),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(0.8f).testTag("reg_depot_code_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.designation,
                    onValueChange = { viewModel.onDesignationChange(it) },
                    label = { Text("Designation") },
                    placeholder = { Text("e.g. Senior Conductor") },
                    singleLine = true,
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_designation_input")
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: SECURITY & CREDENTIALS
                SectionTitle(title = "SECURITY & CREDENTIALS", accentColor = RedAccent)

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password (min 6 chars) *") },
                    placeholder = { Text("Create strong password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = RedAccent) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChange(it) },
                    label = { Text("Confirm Password *") },
                    placeholder = { Text("Re-enter password") },
                    leadingIcon = { Icon(Icons.Default.Lock, null, tint = RedAccent) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    colors = customTextFieldColors(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input")
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SUBMIT BUTTON
                Button(
                    onClick = { viewModel.register { } },
                    enabled = !state.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanAccent,
                        contentColor = DarkNavyBg,
                        disabledContainerColor = CyanAccent.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("register_submit_button")
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = DarkNavyBg,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "CREATE ACCOUNT",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            FooterBranding()
        }
    }

    // Success Dialog on registration
    if (state.isRegistrationComplete) {
        AlertDialog(
            onDismissRequest = { onNavigateBackToLogin() },
            title = {
                Text(
                    text = "Registration Successful",
                    color = GreenAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Account created successfully. Please login to continue.",
                    color = TextWhite,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { onNavigateBackToLogin() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg),
                    modifier = Modifier.testTag("dialog_proceed_login_button")
                ) {
                    Text("Proceed to Login")
                }
            },
            containerColor = DarkNavyCard
        )
    }
}

@Composable
private fun SectionTitle(title: String, accentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accentColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = accentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = CyanAccent,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = CyanAccent,
    unfocusedLabelColor = TextGray,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    cursorColor = CyanAccent
)
