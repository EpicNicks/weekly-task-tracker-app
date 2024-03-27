package com.aspirant.weeklytasktrackerapp.view.tasktracker

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

@Composable
fun TodayScreen(viewModel: TodayViewModel) {

    Box() {
        Text("TODO")
    }
}

class TodayViewModel(
    onNavigateToLogin: () -> Unit
) : ViewModel() {

}