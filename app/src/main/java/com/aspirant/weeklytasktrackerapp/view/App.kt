package com.aspirant.weeklytasktrackerapp.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspirant.weeklytasktrackerapp.model.auth.SharedPreferencesAuthService
import com.aspirant.weeklytasktrackerapp.view.tasktracker.TodayScreen
import com.aspirant.weeklytasktrackerapp.view.tasktracker.TodayViewModel

@Composable
fun App(authService: SharedPreferencesAuthService) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(
                    onNavigateToTaskTracker = { navController.navigate("taskTrackerToday") },
                    onNavigateToRegister = { navController.navigate("register") },
                    authService = authService
                ),
            )
        }
        composable("register") { }
        composable("taskTrackerToday") {
            TodayScreen(
                viewModel = TodayViewModel(
                    onNavigateToLogin = { navController.navigate("login") },
                ),
            )
        }
    }
}