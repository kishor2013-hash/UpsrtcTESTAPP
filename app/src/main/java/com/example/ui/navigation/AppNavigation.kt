package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.admin.AdminViewModel
import com.example.ui.screens.dashboard.DashboardScreen
import com.example.ui.screens.dashboard.DashboardViewModel
import com.example.ui.screens.feedduty.FeedDutyScreen
import com.example.ui.screens.feedduty.FeedDutyViewModel
import com.example.ui.screens.login.LoginScreen
import com.example.ui.screens.login.LoginViewModel
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.register.RegisterScreen
import com.example.ui.screens.register.RegisterViewModel
import com.example.ui.screens.showdata.ShowDataScreen
import com.example.ui.screens.showdata.ShowDataViewModel

object AppDestinations {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val FEED_DUTY = "feed_duty"
    const val SHOW_DATA = "show_data"
    const val PROFILE = "profile"
    const val ADMIN = "admin"
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    registerViewModel: RegisterViewModel,
    dashboardViewModel: DashboardViewModel,
    feedDutyViewModel: FeedDutyViewModel,
    showDataViewModel: ShowDataViewModel,
    profileViewModel: ProfileViewModel,
    adminViewModel: AdminViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.LOGIN, // MUST always start at login
        modifier = modifier
    ) {
        composable(AppDestinations.LOGIN) {
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(AppDestinations.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(AppDestinations.DASHBOARD) {
                        popUpTo(AppDestinations.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.REGISTER) {
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBackToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.DASHBOARD) {
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToFeedDuty = {
                    navController.navigate(AppDestinations.FEED_DUTY)
                },
                onNavigateToShowData = {
                    navController.navigate(AppDestinations.SHOW_DATA)
                },
                onNavigateToProfile = {
                    navController.navigate(AppDestinations.PROFILE)
                },
                onNavigateToAdmin = {
                    navController.navigate(AppDestinations.ADMIN)
                },
                onLogout = {
                    navController.navigate(AppDestinations.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(AppDestinations.FEED_DUTY) {
            FeedDutyScreen(
                viewModel = feedDutyViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToLogs = {
                    navController.navigate(AppDestinations.SHOW_DATA) {
                        popUpTo(AppDestinations.DASHBOARD)
                    }
                }
            )
        }

        composable(AppDestinations.SHOW_DATA) {
            ShowDataScreen(
                viewModel = showDataViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.PROFILE) {
            ProfileScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(AppDestinations.ADMIN) {
            AdminScreen(
                viewModel = adminViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
