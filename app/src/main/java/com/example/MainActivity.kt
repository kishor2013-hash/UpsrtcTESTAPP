package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.remote.FirebaseManager
import com.example.data.repository.AuditRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.DutyRepository
import com.example.data.repository.SettingsRepository
import com.example.ui.navigation.AppNavigation
import com.example.ui.screens.admin.AdminViewModel
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.feedduty.FeedDutyViewModel
import com.example.ui.screens.login.LoginViewModel
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.register.RegisterViewModel
import com.example.ui.screens.showdata.ShowDataViewModel
import com.example.ui.theme.DarkNavyBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize database and infrastructure layers
        val database = AppDatabase.getDatabase(applicationContext)
        val firebaseManager = FirebaseManager(applicationContext)

        val auditRepository = AuditRepository(database)
        val authRepository = AuthRepository(
            context = applicationContext,
            database = database,
            firebaseManager = firebaseManager
        )
        val dutyRepository = DutyRepository(
            database = database,
            firebaseManager = firebaseManager
        )
        val settingsRepository = SettingsRepository(database)

        // Create ViewModels
        val loginViewModel = LoginViewModel(authRepository)
        val registerViewModel = RegisterViewModel(authRepository)
        val dashboardViewModel = DashboardViewModel(authRepository, settingsRepository)
        val feedDutyViewModel = FeedDutyViewModel(authRepository, dutyRepository)
        val showDataViewModel = ShowDataViewModel(authRepository, dutyRepository)
        val profileViewModel = ProfileViewModel(authRepository)
        val adminViewModel = AdminViewModel(
            authRepository = authRepository,
            dutyRepository = dutyRepository,
            settingsRepository = settingsRepository,
            auditRepository = auditRepository,
            firebaseManager = firebaseManager
        )

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkNavyBg
                ) {
                    AppNavigation(
                        loginViewModel = loginViewModel,
                        registerViewModel = registerViewModel,
                        dashboardViewModel = dashboardViewModel,
                        feedDutyViewModel = feedDutyViewModel,
                        showDataViewModel = showDataViewModel,
                        profileViewModel = profileViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
