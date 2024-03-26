package com.aspirant.weeklytasktrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import com.aspirant.weeklytasktrackerapp.model.auth.SharedPreferencesAuthService
import com.aspirant.weeklytasktrackerapp.ui.theme.WeeklyTaskTrackerAppTheme
import com.aspirant.weeklytasktrackerapp.view.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WeeklyTaskTrackerAppTheme {
                // Provide a ViewModel to the content
                val authService = remember { SharedPreferencesAuthService(this) }
                Surface {
                    App(authService = authService)
                }
            }
        }
    }
}