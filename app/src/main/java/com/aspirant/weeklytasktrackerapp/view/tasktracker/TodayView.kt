package com.aspirant.weeklytasktrackerapp.view.tasktracker

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.ViewModel
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService

@Composable
fun TodayScreen(viewModel: TodayViewModel) {

    Box() {
        viewModel.getToken()?.let { Text(it) }
    }
}

class TodayViewModel(
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit
) : ViewModel() {

    fun getToken() = authService.getAuthToken()
}