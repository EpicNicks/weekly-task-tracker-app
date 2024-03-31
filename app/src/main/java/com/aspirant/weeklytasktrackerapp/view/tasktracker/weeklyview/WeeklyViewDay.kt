package com.aspirant.weeklytasktrackerapp.view.tasktracker.weeklyview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.aspirant.weeklytasktrackerapp.model.auth.AuthService
import com.aspirant.weeklytasktrackerapp.model.entity.response.DailyLog
import com.aspirant.weeklytasktrackerapp.model.entity.response.Task
import com.aspirant.weeklytasktrackerapp.utils.dateFormat
import com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview.DailyLogCard
import com.aspirant.weeklytasktrackerapp.view.tasktracker.todayview.DailyLogCardViewModel
import java.time.LocalDate

@Composable
fun WeeklyViewDay(viewModel: WeeklyViewDayViewModel) {

    val isToday = LocalDate.now() == viewModel.getDate()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // HEADER
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isToday)
                        Color(red = 0, green = 179, blue = 255)
                    else Color(red = 140, green = 221, blue = 255)
                )
        ) {
            Text(viewModel.getDate().dayOfWeek.name)
            Text(dateFormat(viewModel.getDate(), "-"))
        }
        // BODY
        Column {
            viewModel.getLogs().map { dailyLog ->
                val task = viewModel.getTasks().find { task ->
                    dailyLog.taskId == task.id
                } ?: return
                DailyLogCard(
                    viewModel = DailyLogCardViewModel(
                        authService = viewModel.getAuthService(),
                        task = task,
                        onNavigateToLogin = viewModel.getOnNavigateToLogin(),
                        date = viewModel.getDate()
                    )
                )
                // add log for task card
            }
        }
    }
}

class WeeklyViewDayViewModel(
    private val date: LocalDate,
    private val logs: List<DailyLog>,
    private val tasks: List<Task>,
    private val authService: AuthService,
    private val onNavigateToLogin: () -> Unit
) : ViewModel() {

    fun getDate() = date
    fun getLogs() = logs.toList()
    fun getTasks() = tasks
    fun getAuthService() = authService
    fun getOnNavigateToLogin() = onNavigateToLogin
}