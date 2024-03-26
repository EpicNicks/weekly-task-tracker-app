package com.aspirant.weeklytasktrackerapp.view

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aspirant.weeklytasktrackerapp.model.auth.SharedPreferencesAuthService

@Composable
fun App(authService: SharedPreferencesAuthService) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = LoginViewModel(
                    onNavigateToTaskTracker = { navController.navigate("taskTracker") },
                    onNavigateToRegister = { navController.navigate("register") },
                    authService = authService
                )
            )
        }
        composable("register") { }
        composable("taskTracker") { }
    }
}