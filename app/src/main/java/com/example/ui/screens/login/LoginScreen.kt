package com.example.ui.screens.login

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.FooterBranding
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.DarkNavyCard
import com.example.ui.theme.DarkNavySurface
import com.example.ui.theme.GreenAccent
import com.example.ui.theme.RedAccent
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.YellowAccent

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = DarkNavyBg,
        modifier = modifier.fillMaxSize().imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Logo
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(CyanAccent.copy(alpha = 0.35f), Color(0xFF00384D))
                            )
                        )
                        .border(2.dp, CyanAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = "UPSRTC Logo",
                        tint = CyanAccent,
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "UPSRCTC ROADWAYS",
                    color = CyanAccent,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )

                Text(
                    text = "DIGITAL DUTY PORTAL V4.0",
                    color = YellowAccent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Authorized Employee Authentication Gateway",
                    color = TextGray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Error / Success Message Banners
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
                                .testTag("login_error_banner")
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

                AnimatedVisibility(visible = state.successMessage != null) {
                    state.successMessage?.let { msg ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GreenAccent.copy(alpha = 0.15f))
                                .border(1.dp, GreenAccent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .testTag("login_success_banner")
                        ) {
                            Text(
                                text = msg,
                                color = GreenAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Login Form Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkNavySurface)
                        .border(1.dp, Color(0xFF1E3A8A), RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Login to your account",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login ID / Email / CND
                        OutlinedTextField(
                            value = state.loginId,
                            onValueChange = { viewModel.onLoginIdChange(it) },
                            label = { Text("Login ID / Email / CND / Mobile") },
                            placeholder = { Text("Enter Email or CND/Employee ID") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Badge,
                                    contentDescription = null,
                                    tint = CyanAccent
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = CyanAccent,
                                unfocusedLabelColor = TextGray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = CyanAccent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_id_input")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password
                        OutlinedTextField(
                            value = state.password,
                            onValueChange = { viewModel.onPasswordChange(it) },
                            label = { Text("Password") },
                            placeholder = { Text("Enter your secure password") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyanAccent
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { viewModel.togglePasswordVisibility() },
                                    modifier = Modifier.testTag("toggle_password_visibility")
                                ) {
                                    Icon(
                                        imageVector = if (state.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle password visibility",
                                        tint = TextGray
                                    )
                                }
                            },
                            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.login { onLoginSuccess() }
                                }
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanAccent,
                                unfocusedBorderColor = BorderDark,
                                focusedLabelColor = CyanAccent,
                                unfocusedLabelColor = TextGray,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite,
                                cursorColor = CyanAccent
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Remember Me & Forgot Password
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { viewModel.onRememberMeChange(!state.rememberMe) }
                            ) {
                                Checkbox(
                                    checked = state.rememberMe,
                                    onCheckedChange = { viewModel.onRememberMeChange(it) },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = CyanAccent,
                                        uncheckedColor = TextMuted,
                                        checkmarkColor = DarkNavyBg
                                    ),
                                    modifier = Modifier.testTag("remember_me_checkbox")
                                )
                                Text(
                                    text = "Remember Me",
                                    color = TextGray,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                text = "Forgot Password?",
                                color = YellowAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .clickable { viewModel.openForgotPasswordDialog() }
                                    .testTag("forgot_password_button")
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // LOGIN BUTTON
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.login { onLoginSuccess() }
                            },
                            enabled = !state.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanAccent,
                                contentColor = DarkNavyBg,
                                disabledContainerColor = CyanAccent.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("login_submit_button")
                        ) {
                            if (state.isLoading) {
                                CircularProgressIndicator(
                                    color = DarkNavyBg,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "LOGIN",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Sign In with Google
                        Button(
                            onClick = {
                                // Google Sign in action
                                viewModel.login { onLoginSuccess() }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF152A52),
                                contentColor = TextWhite
                            ),
                            shape = RoundedCornerShape(50.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .border(1.dp, Color(0xFF2563EB), RoundedCornerShape(50.dp))
                                .testTag("google_login_button")
                        ) {
                            Text(
                                text = "Sign in with Google",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // New Employee Sign Up Link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "New Employee? ",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Create Account",
                        color = CyanAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { onNavigateToRegister() }
                            .testTag("signup_redirect_button")
                    )
                }
            }

            // Footer
            FooterBranding()
        }
    }

    // Forgot Password Dialog
    if (state.isForgotPasswordDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.closeForgotPasswordDialog() },
            title = {
                Text(
                    text = "Reset Password",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter your registered Email or CND/Employee ID along with your new password.",
                        color = TextGray,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.forgotPasswordInput,
                        onValueChange = { viewModel.onForgotPasswordInputChange(it) },
                        label = { Text("Email or Employee ID") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.forgotPasswordNewPass,
                        onValueChange = { viewModel.onForgotPasswordNewPassChange(it) },
                        label = { Text("New Password (min 6 chars)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.forgotPasswordError?.let { err ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = err, color = RedAccent, fontSize = 12.sp)
                    }

                    state.forgotPasswordMessage?.let { msg ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = msg, color = GreenAccent, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetPassword() },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkNavyBg)
                ) {
                    Text("Update Password")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeForgotPasswordDialog() }) {
                    Text("Close", color = TextGray)
                }
            },
            containerColor = DarkNavyCard
        )
    }
}
